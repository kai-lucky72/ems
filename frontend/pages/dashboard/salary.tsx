import { useState, useEffect } from 'react';
import { NextPage } from 'next';
import Layout from '@/components/Layout';
import ProtectedRoute from '@/components/auth/ProtectedRoute';
import SalaryForm from '@/components/SalaryForm';
import { fetchEmployees, fetchSalaries, createSalary, updateSalary } from '@/lib/api';
import { Salary, Employee, Deduction, SalaryFormData } from '@/types';

const SalaryPage: NextPage = () => {
  const [salaries, setSalaries] = useState<Salary[]>([]);
  const [employees, setEmployees] = useState<Employee[]>([]);
  const [selectedSalary, setSelectedSalary] = useState<Salary | null>(null);
  const [selectedEmployeeId, setSelectedEmployeeId] = useState<number | null>(null);
  const [isFormOpen, setIsFormOpen] = useState(false);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [searchQuery, setSearchQuery] = useState('');
  const [filterDepartment, setFilterDepartment] = useState<string>('');

  useEffect(() => {
    const loadData = async () => {
      try {
        setLoading(true);
        const [salariesData, employeesData] = await Promise.all([
          fetchSalaries(),
          fetchEmployees(),
        ]);
        
        setSalaries(salariesData);
        
        // Only show active employees
        const activeEmployees = employeesData.filter((emp: Employee) => emp.isActive);
        setEmployees(activeEmployees);
        
        setError('');
      } catch (err: any) {
        console.error('Failed to fetch data:', err);
        setError('Failed to load salary data. Please try again later.');
      } finally {
        setLoading(false);
      }
    };

    loadData();
  }, []);

  // Get all unique departments from employees
  const departments = Array.from(new Set(employees.map(emp => emp.departmentName)));

  const handleCreateSalary = () => {
    setSelectedSalary(null);
    setSelectedEmployeeId(null);
    setIsFormOpen(true);
  };

  const handleEditSalary = async (salary: Salary) => {
    setSelectedSalary(salary);
    setSelectedEmployeeId(salary.employeeId);
    setIsFormOpen(true);
  };

  const handleSubmitForm = async (formData: SalaryFormData) => {
    try {
      setLoading(true);
      
      let updatedSalary;
      
      if (selectedSalary) {
        // Update existing salary
        updatedSalary = await updateSalary(selectedSalary.id, formData);
        
        // Update salaries state
        setSalaries(prev => 
          prev.map(salary => 
            salary.id === selectedSalary.id ? updatedSalary : salary
          )
        );
      } else {
        // Create new salary
        updatedSalary = await createSalary(formData);
        
        // Add to salaries state
        setSalaries(prev => [...prev, updatedSalary]);
      }
      
      setIsFormOpen(false);
      setSelectedSalary(null);
      setSelectedEmployeeId(null);
      setError('');
    } catch (err: any) {
      console.error('Failed to save salary:', err);
      throw new Error(err.response?.data?.message || 'Failed to save salary information');
    } finally {
      setLoading(false);
    }
  };

  // Calculate total deductions amount
  const calculateTotalDeductions = (deductions: Deduction[], grossSalary: number) => {
    return deductions.reduce((total, deduction) => {
      const amount = deduction.isPercentage
        ? (grossSalary * deduction.value) / 100
        : deduction.value;
      return total + amount;
    }, 0);
  };

  // Filter salaries based on search query and department filter
  const filteredSalaries = salaries.filter(salary => {
    const matchesSearch = 
      salary.employeeName.toLowerCase().includes(searchQuery.toLowerCase()) ||
      salary.departmentName.toLowerCase().includes(searchQuery.toLowerCase());
    
    const matchesDepartment = filterDepartment === '' || salary.departmentName === filterDepartment;
    
    return matchesSearch && matchesDepartment;
  });

  return (
    <ProtectedRoute requiredRole="MANAGER">
      <Layout>
        <div className="space-y-6">
          <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between">
            <h1 className="text-2xl font-bold">Salary Management</h1>
            <button
              onClick={handleCreateSalary}
              className="mt-3 sm:mt-0 btn btn-primary"
            >
              Create Salary Record
            </button>
          </div>

          {error && (
            <div className="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded">
              {error}
            </div>
          )}

          <div className="bg-white rounded-lg shadow overflow-hidden">
            <div className="p-4 border-b">
              <div className="flex flex-col md:flex-row space-y-3 md:space-y-0 md:space-x-4">
                <div className="flex-1">
                  <input
                    type="text"
                    placeholder="Search salaries..."
                    value={searchQuery}
                    onChange={(e) => setSearchQuery(e.target.value)}
                    className="input"
                  />
                </div>
                <div className="w-full md:w-64">
                  <select
                    value={filterDepartment}
                    onChange={(e) => setFilterDepartment(e.target.value)}
                    className="input"
                  >
                    <option value="">All Departments</option>
                    {departments.map((dept) => (
                      <option key={dept} value={dept}>
                        {dept}
                      </option>
                    ))}
                  </select>
                </div>
              </div>
            </div>

            {loading && filteredSalaries.length === 0 ? (
              <div className="flex justify-center py-12">
                <div className="animate-spin rounded-full h-12 w-12 border-t-2 border-b-2 border-primary"></div>
              </div>
            ) : filteredSalaries.length === 0 ? (
              <div className="p-6 text-center text-gray-500">
                No salary records found matching your criteria.
              </div>
            ) : (
              <div className="overflow-x-auto">
                <table className="min-w-full divide-y divide-gray-200">
                  <thead className="bg-gray-50">
                    <tr>
                      <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                        Employee
                      </th>
                      <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                        Department
                      </th>
                      <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                        Gross Salary
                      </th>
                      <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                        Deductions
                      </th>
                      <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                        Net Salary
                      </th>
                      <th className="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase tracking-wider">
                        Actions
                      </th>
                    </tr>
                  </thead>
                  <tbody className="bg-white divide-y divide-gray-200">
                    {filteredSalaries.map((salary) => {
                      const totalDeductions = calculateTotalDeductions(
                        salary.deductions,
                        salary.grossSalary
                      );
                      
                      return (
                        <tr key={salary.id}>
                          <td className="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">
                            {salary.employeeName}
                          </td>
                          <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                            {salary.departmentName}
                          </td>
                          <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                            ${salary.grossSalary.toFixed(2)}
                          </td>
                          <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                            ${totalDeductions.toFixed(2)}
                            <span className="text-xs text-gray-400 ml-1">
                              ({salary.deductions.length} items)
                            </span>
                          </td>
                          <td className="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">
                            ${salary.netSalary.toFixed(2)}
                          </td>
                          <td className="px-6 py-4 whitespace-nowrap text-right text-sm font-medium">
                            <button
                              onClick={() => handleEditSalary(salary)}
                              className="text-primary hover:text-primary-dark"
                            >
                              Edit
                            </button>
                          </td>
                        </tr>
                      );
                    })}
                  </tbody>
                </table>
              </div>
            )}
          </div>
        </div>

        {/* Salary Form Modal */}
        {isFormOpen && (
          <div className="fixed inset-0 overflow-y-auto z-50 flex items-center justify-center bg-black bg-opacity-50">
            <div className="bg-white rounded-lg shadow-xl max-w-3xl w-full mx-4 p-6">
              <div className="flex justify-between items-center mb-6">
                <h2 className="text-xl font-bold">
                  {selectedSalary ? 'Edit Salary Record' : 'Create Salary Record'}
                </h2>
                <button
                  onClick={() => {
                    setIsFormOpen(false);
                    setSelectedSalary(null);
                    setSelectedEmployeeId(null);
                  }}
                  className="text-gray-400 hover:text-gray-500"
                >
                  <svg className="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M6 18L18 6M6 6l12 12" />
                  </svg>
                </button>
              </div>
              
              <div className="overflow-y-auto max-h-[70vh]">
                <SalaryForm
                  salary={selectedSalary}
                  employeeId={selectedEmployeeId}
                  employees={employees}
                  onSubmit={handleSubmitForm}
                  onCancel={() => {
                    setIsFormOpen(false);
                    setSelectedSalary(null);
                    setSelectedEmployeeId(null);
                  }}
                />
              </div>
            </div>
          </div>
        )}
      </Layout>
    </ProtectedRoute>
  );
};

export default SalaryPage;