import { useState, useEffect } from 'react';
import { NextPage } from 'next';
import Layout from '@/components/Layout';
import DepartmentForm from '@/components/DepartmentForm';
import { fetchDepartments, createDepartment, updateDepartment, deleteDepartment } from '@/lib/api';
import { Department, DepartmentFormData } from '@/types';

const DepartmentsPage: NextPage = () => {
  const [departments, setDepartments] = useState<Department[]>([]);
  const [selectedDepartment, setSelectedDepartment] = useState<Department | null>(null);
  const [isFormOpen, setIsFormOpen] = useState(false);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [searchQuery, setSearchQuery] = useState('');

  useEffect(() => {
    const loadDepartments = async () => {
      try {
        setLoading(true);
        const data = await fetchDepartments();
        setDepartments(data);
        setError('');
      } catch (err: any) {
        console.error('Failed to fetch departments:', err);
        setError('Failed to load departments. Please try again later.');
      } finally {
        setLoading(false);
      }
    };

    loadDepartments();
  }, []);

  const handleAddDepartment = () => {
    setSelectedDepartment(null);
    setIsFormOpen(true);
  };

  const handleEditDepartment = (department: Department) => {
    setSelectedDepartment(department);
    setIsFormOpen(true);
  };

  const handleCloseForm = () => {
    setIsFormOpen(false);
    setSelectedDepartment(null);
  };

  const handleSubmitForm = async (formData: DepartmentFormData) => {
    try {
      if (selectedDepartment) {
        // Update existing department
        const updatedDepartment = await updateDepartment(selectedDepartment.id, formData);
        setDepartments(prev => prev.map(dep => dep.id === selectedDepartment.id ? updatedDepartment : dep));
      } else {
        // Create new department
        const newDepartment = await createDepartment(formData);
        setDepartments(prev => [...prev, newDepartment]);
      }
      setIsFormOpen(false);
      setSelectedDepartment(null);
    } catch (err: any) {
      throw new Error(err.response?.data?.message || 'Failed to save department');
    }
  };

  const handleDeleteDepartment = async (id: number) => {
    if (!confirm('Are you sure you want to delete this department?')) {
      return;
    }

    try {
      await deleteDepartment(id);
      setDepartments(prev => prev.filter(dep => dep.id !== id));
    } catch (err: any) {
      console.error('Failed to delete department:', err);
      setError(err.response?.data?.message || 'Failed to delete department');
    }
  };

  // Filter departments based on search query
  const filteredDepartments = departments.filter(department => 
    department.name.toLowerCase().includes(searchQuery.toLowerCase())
  );

  // Calculate budget usage percentage
  const getBudgetPercentage = (department: Department) => {
    return (department.currentExpenses / department.budget) * 100;
  };

  // Get appropriate CSS class based on budget usage
  const getBudgetStatusClass = (department: Department) => {
    const percentage = getBudgetPercentage(department);
    if (percentage >= 90) return 'bg-red-100 text-red-800'; 
    if (percentage >= 70) return 'bg-yellow-100 text-yellow-800';
    return 'bg-green-100 text-green-800';
  };

  return (
    <Layout>
      <div className="space-y-6">
        <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between">
          <h1 className="text-2xl font-bold">Departments</h1>
          <button
            onClick={handleAddDepartment}
            className="mt-3 sm:mt-0 btn btn-primary"
          >
            Add Department
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
                  placeholder="Search departments..."
                  value={searchQuery}
                  onChange={(e) => setSearchQuery(e.target.value)}
                  className="input"
                />
              </div>
            </div>
          </div>

          {loading ? (
            <div className="flex justify-center py-12">
              <div className="animate-spin rounded-full h-12 w-12 border-t-2 border-b-2 border-primary"></div>
            </div>
          ) : filteredDepartments.length === 0 ? (
            <div className="p-6 text-center text-gray-500">
              No departments found matching your criteria.
            </div>
          ) : (
            <div className="overflow-x-auto">
              <table className="min-w-full divide-y divide-gray-200">
                <thead className="bg-gray-50">
                  <tr>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                      Department Name
                    </th>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                      Budget
                    </th>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                      Current Expenses
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
                  {filteredDepartments.map((department) => (
                    <tr key={department.id}>
                      <td className="px-6 py-4 whitespace-nowrap">
                        <div className="text-sm font-medium text-gray-900">{department.name}</div>
                      </td>
                      <td className="px-6 py-4 whitespace-nowrap">
                        <div className="text-sm text-gray-900">
                          ${department.budget.toLocaleString()}
                        </div>
                        <div className="text-xs text-gray-500">
                          {department.budgetType === 'MONTHLY' ? 'Monthly' : 'Yearly'}
                        </div>
                      </td>
                      <td className="px-6 py-4 whitespace-nowrap">
                        <div className="text-sm text-gray-900">
                          ${department.currentExpenses.toLocaleString()}
                        </div>
                        <div className="text-xs text-gray-500">
                          {getBudgetPercentage(department).toFixed(1)}% of budget
                        </div>
                      </td>
                      <td className="px-6 py-4 whitespace-nowrap">
                        <span className={`px-2 inline-flex text-xs leading-5 font-semibold rounded-full ${getBudgetStatusClass(department)}`}>
                          {getBudgetPercentage(department) >= 90 
                            ? 'Over Budget' 
                            : getBudgetPercentage(department) >= 70 
                              ? 'Warning' 
                              : 'Good'}
                        </span>
                      </td>
                      <td className="px-6 py-4 whitespace-nowrap text-right text-sm font-medium">
                        <button
                          onClick={() => handleEditDepartment(department)}
                          className="text-primary hover:text-primary-dark mr-4"
                        >
                          Edit
                        </button>
                        <button
                          onClick={() => handleDeleteDepartment(department.id)}
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

      {/* Department Form Modal */}
      {isFormOpen && (
        <div className="fixed inset-0 overflow-y-auto z-50 flex items-center justify-center bg-black bg-opacity-50">
          <div className="bg-white rounded-lg shadow-xl max-w-lg w-full mx-4 p-6">
            <div className="flex justify-between items-center mb-6">
              <h2 className="text-xl font-bold">
                {selectedDepartment ? 'Edit Department' : 'Add New Department'}
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
            <DepartmentForm
              department={selectedDepartment}
              onSubmit={handleSubmitForm}
              onCancel={handleCloseForm}
            />
          </div>
        </div>
      )}
    </Layout>
  );
};

export default DepartmentsPage;