import { useState, useEffect } from 'react';
import { NextPage } from 'next';
import Layout from '@/components/Layout';
import SalaryForm from '@/components/SalaryForm';
import { fetchEmployees, fetchSalaries, fetchSalaryByEmployee, createSalary, updateSalary } from '@/lib/api';
import { Salary, Deduction, SalaryFormData } from '@/types';

interface Employee {
  id: number;
  name: string;
  departmentName: string;
  role: string;
  isActive: boolean;
}

const SalaryPage: NextPage = () => {
  const [salaries, setSalaries] = useState<Salary[]>([]);
  const [employees, setEmployees] = useState<Employee[]>([]);
  const [selectedSalary, setSelectedSalary] = useState<Salary | null>(null);
  const [selectedEmployeeId, setSelectedEmployeeId] = useState<number | null>(null);
  const [isFormOpen, setIsFormOpen] = useState(false);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [searchQuery, setSearchQuery] = useState('');
  const [filterDepartment, setFilterDepartment] = useState('');
  const [departmentFilters, setDepartmentFilters] = useState<string[]>([]);

  useEffect(() => {
    const loadData = async () => {
      try {
        setLoading(true);
        const [salariesData, employeesData] = await Promise.all([
          fetchSalaries(),
          fetchEmployees(),
        ]);
        
        setSalaries(salariesData);
        setEmployees(employeesData);
        
        // Extract unique departments for filtering
        const departments = [...new Set(employeesData.map(emp => emp.departmentName))];
        setDepartmentFilters(departments);
        
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

  const handleAddSalary = () => {
    setSelectedSalary(null);
    setSelectedEmployeeId(null);
    setIsFormOpen(true);
  };

  const handleEditSalary = async (salary: Salary) => {
    setSelectedSalary(salary);
    setSelectedEmployeeId(salary.employeeId);
    setIsFormOpen(true);
  };

  const handleAddForEmployee = (employeeId: number) => {
    setSelectedSalary(null);
    setSelectedEmployeeId(employeeId);
    setIsFormOpen(true);
  };

  const handleCloseForm = () => {
    setIsFormOpen(false);
    setSelectedSalary(null);
    setSelectedEmployeeId(null);
  };

  const handleSubmitForm = async (formData: SalaryFormData) => {
    try {
      if (selectedSalary) {
        // Update existing salary
        const updatedSalary = await updateSalary(selectedSalary.id, formData);
        setSalaries(prev => prev.map(s => s.id === selectedSalary.id ? updatedSalary : s));
      } else {
        // Create new salary
        const newSalary = await createSalary(formData);
        setSalaries(prev => [...prev, newSalary]);
      }
      setIsFormOpen(false);
      setSelectedSalary(null);
      setSelectedEmployeeId(null);
    } catch (err: any) {
      throw new Error(err.response?.data?.message || 'Failed to save salary');
    }
  };

  // Filter salaries based on search query and department filter
  const filteredSalaries = salaries.filter(salary => {
    const matchesSearch = 
      salary.employeeName.toLowerCase().includes(searchQuery.toLowerCase()) || 
      salary.departmentName.toLowerCase().includes(searchQuery.toLowerCase());
    
    const matchesDepartment = filterDepartment === '' || salary.departmentName === filterDepartment;
    
    return matchesSearch && matchesDepartment;
  });

  // Find employees without salaries for the "Add Salary" section
  const employeesWithoutSalary = employees.filter(employee => 
    !salaries.some(salary => salary.employeeId === employee.id) && employee.isActive
  );

  return (
    <Layout>
      <div className="space-y-6">
        <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between">
          <h1 className="text-2xl font-bold">Salary Management</h1>
          <button
            onClick={handleAddSalary}
            className="mt-3 sm:mt-0 btn btn-primary"
          >
            Add New Salary
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
                  placeholder="Search by employee or department..."
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
                  {departmentFilters.map((dept) => (
                    <option key={dept} value={dept}>
                      {dept}
                    </option>
                  ))}
                </select>
              </div>
            </div>
          </div>

          {loading ? (
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
                  {filteredSalaries.map((salary) => (
                    <tr key={salary.id}>
                      <td className="px-6 py-4 whitespace-nowrap">
                        <div className="text-sm font-medium text-gray-900">{salary.employeeName}</div>
                      </td>
                      <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                        {salary.departmentName}
                      </td>
                      <td className="px-6 py-4 whitespace-nowrap">
                        <div className="text-sm text-gray-900">
                          ${salary.grossSalary.toLocaleString()}
                        </div>
                      </td>
                      <td className="px-6 py-4 whitespace-nowrap">
                        <div className="text-sm text-gray-900">
                          {salary.deductions.length} items
                        </div>
                        <div className="text-xs text-gray-500">
                          ${(salary.grossSalary - salary.netSalary).toLocaleString()}
                        </div>
                      </td>
                      <td className="px-6 py-4 whitespace-nowrap">
                        <div className="text-sm font-semibold text-blue-600">
                          ${salary.netSalary.toLocaleString()}
                        </div>
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
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </div>

        {/* Employees without salary section */}
        {employeesWithoutSalary.length > 0 && (
          <div className="bg-white rounded-lg shadow overflow-hidden">
            <div className="px-6 py-4 border-b">
              <h2 className="text-lg font-medium">Employees Without Salary</h2>
            </div>
            <div className="divide-y divide-gray-200">
              {employeesWithoutSalary.map((employee) => (
                <div key={employee.id} className="px-6 py-4 flex items-center justify-between">
                  <div>
                    <div className="text-sm font-medium">{employee.name}</div>
                    <div className="text-xs text-gray-500">
                      {employee.departmentName} - {employee.role}
                    </div>
                  </div>
                  <button
                    onClick={() => handleAddForEmployee(employee.id)}
                    className="btn btn-sm btn-secondary"
                  >
                    Add Salary
                  </button>
                </div>
              ))}
            </div>
          </div>
        )}
      </div>

      {/* Salary Form Modal */}
      {isFormOpen && (
        <div className="fixed inset-0 overflow-y-auto z-50 flex items-center justify-center bg-black bg-opacity-50">
          <div className="bg-white rounded-lg shadow-xl max-w-4xl w-full mx-4 p-6">
            <div className="flex justify-between items-center mb-6">
              <h2 className="text-xl font-bold">
                {selectedSalary ? 'Edit Salary' : 'Add New Salary'}
              </h2>
              <button
                onClick={handleCloseForm}
                className="text-gray-400 hover:text-gray-500"
              >
                <svg className="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M6 18L18 6M6 6l12 12" />
                </svg>
              </button>
            </div>
            <SalaryForm
              salary={selectedSalary}
              employeeId={selectedEmployeeId}
              employees={employees}
              onSubmit={handleSubmitForm}
              onCancel={handleCloseForm}
            />
          </div>
        </div>
      )}
    </Layout>
  );
};

export default SalaryPage;