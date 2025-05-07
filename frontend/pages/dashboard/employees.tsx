import { useState, useEffect } from 'react';
import { NextPage } from 'next';
import Layout from '@/components/Layout';
import EmployeeForm from '@/components/EmployeeForm';
import { fetchEmployees, fetchDepartments, createEmployee, updateEmployee, deleteEmployee, updateEmployeeStatus } from '@/lib/api';
import { Employee, Department, EmployeeFormData } from '@/types';

const EmployeesPage: NextPage = () => {
  const [employees, setEmployees] = useState<Employee[]>([]);
  const [departments, setDepartments] = useState<Department[]>([]);
  const [selectedEmployee, setSelectedEmployee] = useState<Employee | null>(null);
  const [isEmployeeFormOpen, setIsEmployeeFormOpen] = useState(false);
  const [isStatusFormOpen, setIsStatusFormOpen] = useState(false);
  const [statusFormData, setStatusFormData] = useState({
    isActive: true,
    inactiveFrom: '',
    inactiveTo: '',
  });
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [searchQuery, setSearchQuery] = useState('');
  const [filterDepartment, setFilterDepartment] = useState<number | ''>('');
  const [filterStatus, setFilterStatus] = useState<boolean | ''>('');

  useEffect(() => {
    const loadData = async () => {
      try {
        setLoading(true);
        const [employeesData, departmentsData] = await Promise.all([
          fetchEmployees(),
          fetchDepartments(),
        ]);
        setEmployees(employeesData);
        setDepartments(departmentsData);
        setError('');
      } catch (err: any) {
        console.error('Failed to fetch data:', err);
        setError('Failed to load employees data. Please try again later.');
      } finally {
        setLoading(false);
      }
    };

    loadData();
  }, []);

  const handleAddEmployee = () => {
    setSelectedEmployee(null);
    setIsEmployeeFormOpen(true);
  };

  const handleEditEmployee = (employee: Employee) => {
    setSelectedEmployee(employee);
    setIsEmployeeFormOpen(true);
  };

  const handleCloseForm = () => {
    setIsEmployeeFormOpen(false);
    setSelectedEmployee(null);
  };

  const handleSubmitEmployeeForm = async (formData: EmployeeFormData) => {
    try {
      if (selectedEmployee) {
        // Update existing employee
        const updatedEmployee = await updateEmployee(selectedEmployee.id, formData);
        setEmployees(prev => prev.map(emp => emp.id === selectedEmployee.id ? updatedEmployee : emp));
      } else {
        // Create new employee
        const newEmployee = await createEmployee(formData);
        setEmployees(prev => [...prev, newEmployee]);
      }
      setIsEmployeeFormOpen(false);
      setSelectedEmployee(null);
    } catch (err: any) {
      throw new Error(err.response?.data?.message || 'Failed to save employee');
    }
  };

  const handleDeleteEmployee = async (id: number) => {
    if (!confirm('Are you sure you want to delete this employee?')) {
      return;
    }

    try {
      await deleteEmployee(id);
      setEmployees(prev => prev.filter(emp => emp.id !== id));
    } catch (err: any) {
      console.error('Failed to delete employee:', err);
      setError(err.response?.data?.message || 'Failed to delete employee');
    }
  };

  const handleOpenStatusForm = (employee: Employee) => {
    setSelectedEmployee(employee);
    setStatusFormData({
      isActive: employee.isActive,
      inactiveFrom: employee.inactiveFrom ? employee.inactiveFrom.split('T')[0] : '',
      inactiveTo: employee.inactiveTo ? employee.inactiveTo.split('T')[0] : '',
    });
    setIsStatusFormOpen(true);
  };

  const handleStatusChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value, type, checked } = e.target;
    setStatusFormData(prev => ({
      ...prev,
      [name]: type === 'checkbox' ? checked : value,
    }));
  };

  const handleSubmitStatusForm = async (e: React.FormEvent) => {
    e.preventDefault();
    
    if (!selectedEmployee) return;

    try {
      const updatedEmployee = await updateEmployeeStatus(selectedEmployee.id, {
        isActive: statusFormData.isActive,
        inactiveFrom: statusFormData.inactiveFrom || null,
        inactiveTo: statusFormData.inactiveTo || null,
      });

      setEmployees(prev => 
        prev.map(emp => emp.id === selectedEmployee.id ? updatedEmployee : emp)
      );
      
      setIsStatusFormOpen(false);
      setSelectedEmployee(null);
    } catch (err: any) {
      console.error('Failed to update employee status:', err);
      setError(err.response?.data?.message || 'Failed to update employee status');
    }
  };

  // Filter employees based on search query and filters
  const filteredEmployees = employees.filter(employee => {
    const matchesSearch = 
      employee.name.toLowerCase().includes(searchQuery.toLowerCase()) || 
      employee.email.toLowerCase().includes(searchQuery.toLowerCase()) ||
      employee.role.toLowerCase().includes(searchQuery.toLowerCase());
    
    const matchesDepartment = filterDepartment === '' || employee.departmentId === Number(filterDepartment);
    const matchesStatus = filterStatus === '' || employee.isActive === filterStatus;
    
    return matchesSearch && matchesDepartment && matchesStatus;
  });

  return (
    <Layout>
      <div className="space-y-6">
        <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between">
          <h1 className="text-2xl font-bold">Employees</h1>
          <button
            onClick={handleAddEmployee}
            className="mt-3 sm:mt-0 btn btn-primary"
          >
            Add Employee
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
                  placeholder="Search employees..."
                  value={searchQuery}
                  onChange={(e) => setSearchQuery(e.target.value)}
                  className="input"
                />
              </div>
              <div className="w-full md:w-64">
                <select
                  value={filterDepartment.toString()}
                  onChange={(e) => setFilterDepartment(e.target.value === '' ? '' : Number(e.target.value))}
                  className="input"
                >
                  <option value="">All Departments</option>
                  {departments.map((dept) => (
                    <option key={dept.id} value={dept.id}>
                      {dept.name}
                    </option>
                  ))}
                </select>
              </div>
              <div className="w-full md:w-44">
                <select
                  value={filterStatus === '' ? '' : String(filterStatus)}
                  onChange={(e) => {
                    if (e.target.value === '') {
                      setFilterStatus('');
                    } else {
                      setFilterStatus(e.target.value === 'true');
                    }
                  }}
                  className="input"
                >
                  <option value="">All Status</option>
                  <option value="true">Active</option>
                  <option value="false">Inactive</option>
                </select>
              </div>
            </div>
          </div>

          {loading ? (
            <div className="flex justify-center py-12">
              <div className="animate-spin rounded-full h-12 w-12 border-t-2 border-b-2 border-primary"></div>
            </div>
          ) : filteredEmployees.length === 0 ? (
            <div className="p-6 text-center text-gray-500">
              No employees found matching your criteria.
            </div>
          ) : (
            <div className="overflow-x-auto">
              <table className="min-w-full divide-y divide-gray-200">
                <thead className="bg-gray-50">
                  <tr>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                      Name
                    </th>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                      Contact
                    </th>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                      Role
                    </th>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                      Department
                    </th>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                      Status
                    </th>
                    <th className="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase tracking-wider">
                      Actions
                    </th>
                  </tr>
                </thead>
                <tbody className="bg-white divide-y divide-gray-200">
                  {filteredEmployees.map((employee) => (
                    <tr key={employee.id}>
                      <td className="px-6 py-4 whitespace-nowrap">
                        <div className="text-sm font-medium text-gray-900">{employee.name}</div>
                        <div className="text-sm text-gray-500">
                          {employee.contractType === 'FULL_TIME' ? 'Full Time' : 
                           employee.contractType === 'PART_TIME' ? 'Part Time' : 'Remote'}
                        </div>
                      </td>
                      <td className="px-6 py-4 whitespace-nowrap">
                        <div className="text-sm text-gray-900">{employee.email}</div>
                        <div className="text-sm text-gray-500">{employee.phone}</div>
                      </td>
                      <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                        {employee.role}
                      </td>
                      <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                        {employee.departmentName}
                      </td>
                      <td className="px-6 py-4 whitespace-nowrap">
                        <span
                          className={`px-2 inline-flex text-xs leading-5 font-semibold rounded-full ${
                            employee.isActive
                              ? 'bg-green-100 text-green-800'
                              : 'bg-red-100 text-red-800'
                          }`}
                        >
                          {employee.isActive ? 'Active' : 'Inactive'}
                        </span>
                      </td>
                      <td className="px-6 py-4 whitespace-nowrap text-right text-sm font-medium">
                        <button
                          onClick={() => handleOpenStatusForm(employee)}
                          className="text-indigo-600 hover:text-indigo-900 mr-4"
                        >
                          Status
                        </button>
                        <button
                          onClick={() => handleEditEmployee(employee)}
                          className="text-primary hover:text-primary-dark mr-4"
                        >
                          Edit
                        </button>
                        <button
                          onClick={() => handleDeleteEmployee(employee.id)}
                          className="text-red-600 hover:text-red-900"
                        >
                          Delete
                        </button>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </div>
      </div>

      {/* Employee Form Modal */}
      {isEmployeeFormOpen && (
        <div className="fixed inset-0 overflow-y-auto z-50 flex items-center justify-center bg-black bg-opacity-50">
          <div className="bg-white rounded-lg shadow-xl max-w-4xl w-full mx-4 p-6">
            <div className="flex justify-between items-center mb-6">
              <h2 className="text-xl font-bold">
                {selectedEmployee ? 'Edit Employee' : 'Add New Employee'}
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
            <EmployeeForm
              employee={selectedEmployee}
              departments={departments}
              onSubmit={handleSubmitEmployeeForm}
              onCancel={handleCloseForm}
            />
          </div>
        </div>
      )}

      {/* Status Change Modal */}
      {isStatusFormOpen && selectedEmployee && (
        <div className="fixed inset-0 overflow-y-auto z-50 flex items-center justify-center bg-black bg-opacity-50">
          <div className="bg-white rounded-lg shadow-xl max-w-md w-full mx-4 p-6">
            <div className="flex justify-between items-center mb-6">
              <h2 className="text-xl font-bold">Update Employee Status</h2>
              <button
                onClick={() => setIsStatusFormOpen(false)}
                className="text-gray-400 hover:text-gray-500"
              >
                <svg className="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M6 18L18 6M6 6l12 12" />
                </svg>
              </button>
            </div>

            <form onSubmit={handleSubmitStatusForm} className="space-y-6">
              <div>
                <div className="flex items-center mb-4">
                  <input
                    id="statusIsActive"
                    name="isActive"
                    type="checkbox"
                    checked={statusFormData.isActive}
                    onChange={handleStatusChange}
                    className="h-4 w-4 text-primary focus:ring-primary border-gray-300 rounded"
                  />
                  <label htmlFor="statusIsActive" className="ml-2 block text-sm text-gray-700">
                    Active Employee
                  </label>
                </div>

                {!statusFormData.isActive && (
                  <>
                    <div className="mb-4">
                      <label htmlFor="inactiveFrom" className="label">
                        Inactive From
                      </label>
                      <input
                        id="inactiveFrom"
                        name="inactiveFrom"
                        type="date"
                        value={statusFormData.inactiveFrom}
                        onChange={handleStatusChange}
                        className="input"
                      />
                    </div>

                    <div>
                      <label htmlFor="inactiveTo" className="label">
                        Inactive Until (if applicable)
                      </label>
                      <input
                        id="inactiveTo"
                        name="inactiveTo"
                        type="date"
                        value={statusFormData.inactiveTo}
                        onChange={handleStatusChange}
                        className="input"
                      />
                    </div>
                  </>
                )}
              </div>

              <div className="flex justify-end space-x-3">
                <button
                  type="button"
                  onClick={() => setIsStatusFormOpen(false)}
                  className="btn border border-gray-300 bg-white text-gray-700 hover:bg-gray-50"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  className="btn btn-primary"
                >
                  Save Changes
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </Layout>
  );
};

export default EmployeesPage;