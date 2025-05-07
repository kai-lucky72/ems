import { useState, useEffect } from 'react';
import { NextPage } from 'next';
import Layout from '@/components/Layout';
import ProtectedRoute from '@/components/auth/ProtectedRoute';
import LeaveForm from '@/components/LeaveForm';
import { fetchLeaves, fetchEmployees, createLeave, updateLeaveStatus } from '@/lib/api';
import { Leave, Employee, LeaveFormData, LeaveStatusFormData } from '@/types';

const LeavePage: NextPage = () => {
  const [leaves, setLeaves] = useState<Leave[]>([]);
  const [employees, setEmployees] = useState<Employee[]>([]);
  const [isFormOpen, setIsFormOpen] = useState(false);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [searchQuery, setSearchQuery] = useState('');
  const [filterStatus, setFilterStatus] = useState<string>('');
  const [filterEmployee, setFilterEmployee] = useState<number | ''>('');

  useEffect(() => {
    const loadData = async () => {
      try {
        setLoading(true);
        const [leavesData, employeesData] = await Promise.all([
          fetchLeaves(),
          fetchEmployees(),
        ]);
        setLeaves(leavesData);
        setEmployees(employeesData.filter((emp: Employee) => emp.isActive));
        setError('');
      } catch (err: any) {
        console.error('Failed to fetch data:', err);
        setError('Failed to load leave management data. Please try again later.');
      } finally {
        setLoading(false);
      }
    };

    loadData();
  }, []);

  const handleOpenForm = () => {
    setIsFormOpen(true);
  };

  const handleCloseForm = () => {
    setIsFormOpen(false);
  };

  const handleSubmitLeaveForm = async (formData: LeaveFormData) => {
    try {
      setLoading(true);
      const newLeave = await createLeave(formData);
      setLeaves(prev => [newLeave, ...prev]);
      setIsFormOpen(false);
    } catch (err: any) {
      console.error('Failed to create leave request:', err);
      throw new Error(err.response?.data?.message || 'Failed to create leave request');
    } finally {
      setLoading(false);
    }
  };

  const handleUpdateLeaveStatus = async (leaveId: number, status: 'APPROVED' | 'DENIED') => {
    try {
      setLoading(true);
      const statusData: LeaveStatusFormData = { status };
      const updatedLeave = await updateLeaveStatus(leaveId, statusData);
      
      setLeaves(prev => prev.map(leave => 
        leave.id === leaveId ? updatedLeave : leave
      ));
      
      setError('');
    } catch (err: any) {
      console.error('Failed to update leave status:', err);
      setError(err.response?.data?.message || 'Failed to update leave status');
    } finally {
      setLoading(false);
    }
  };

  // Format date string to more readable format
  const formatDate = (dateString: string) => {
    return new Date(dateString).toLocaleDateString('en-US', {
      year: 'numeric',
      month: 'short',
      day: 'numeric',
    });
  };

  // Filter leaves based on search query and filters
  const filteredLeaves = leaves.filter(leave => {
    const matchesSearch = 
      leave.employeeName.toLowerCase().includes(searchQuery.toLowerCase()) ||
      leave.reason.toLowerCase().includes(searchQuery.toLowerCase());
    
    const matchesStatus = filterStatus === '' || leave.status === filterStatus;
    const matchesEmployee = filterEmployee === '' || leave.employeeId === Number(filterEmployee);
    
    return matchesSearch && matchesStatus && matchesEmployee;
  });

  // Get CSS class for status badge
  const getStatusBadgeClass = (status: string) => {
    switch (status) {
      case 'APPROVED':
        return 'bg-green-100 text-green-800';
      case 'DENIED':
        return 'bg-red-100 text-red-800';
      case 'PENDING':
      default:
        return 'bg-yellow-100 text-yellow-800';
    }
  };

  return (
    <ProtectedRoute requiredRole="MANAGER">
      <Layout>
        <div className="space-y-6">
          <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between">
            <h1 className="text-2xl font-bold">Leave Management</h1>
            <button
              onClick={handleOpenForm}
              className="mt-3 sm:mt-0 btn btn-primary"
            >
              Create Leave Request
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
                    placeholder="Search leave requests..."
                    value={searchQuery}
                    onChange={(e) => setSearchQuery(e.target.value)}
                    className="input"
                  />
                </div>
                <div className="w-full md:w-64">
                  <select
                    value={filterEmployee.toString()}
                    onChange={(e) => setFilterEmployee(e.target.value === '' ? '' : Number(e.target.value))}
                    className="input"
                  >
                    <option value="">All Employees</option>
                    {employees.map((emp) => (
                      <option key={emp.id} value={emp.id}>
                        {emp.name}
                      </option>
                    ))}
                  </select>
                </div>
                <div className="w-full md:w-44">
                  <select
                    value={filterStatus}
                    onChange={(e) => setFilterStatus(e.target.value)}
                    className="input"
                  >
                    <option value="">All Status</option>
                    <option value="PENDING">Pending</option>
                    <option value="APPROVED">Approved</option>
                    <option value="DENIED">Denied</option>
                  </select>
                </div>
              </div>
            </div>

            {loading && filteredLeaves.length === 0 ? (
              <div className="flex justify-center py-12">
                <div className="animate-spin rounded-full h-12 w-12 border-t-2 border-b-2 border-primary"></div>
              </div>
            ) : filteredLeaves.length === 0 ? (
              <div className="p-6 text-center text-gray-500">
                No leave requests found matching your criteria.
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
                        Leave Period
                      </th>
                      <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                        Duration
                      </th>
                      <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                        Reason
                      </th>
                      <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                        Requested On
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
                    {filteredLeaves.map((leave) => {
                      // Calculate duration in days
                      const start = new Date(leave.startDate);
                      const end = new Date(leave.endDate);
                      const diffTime = Math.abs(end.getTime() - start.getTime());
                      const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24)) + 1;
                      
                      return (
                        <tr key={leave.id}>
                          <td className="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">
                            {leave.employeeName}
                          </td>
                          <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                            {formatDate(leave.startDate)} - {formatDate(leave.endDate)}
                          </td>
                          <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                            {diffDays} day{diffDays !== 1 ? 's' : ''}
                          </td>
                          <td className="px-6 py-4 text-sm text-gray-500 max-w-xs truncate">
                            {leave.reason}
                          </td>
                          <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                            {formatDate(leave.createdAt)}
                          </td>
                          <td className="px-6 py-4 whitespace-nowrap">
                            <span className={`px-2 inline-flex text-xs leading-5 font-semibold rounded-full ${getStatusBadgeClass(leave.status)}`}>
                              {leave.status}
                            </span>
                          </td>
                          <td className="px-6 py-4 whitespace-nowrap text-right text-sm font-medium">
                            {leave.status === 'PENDING' && (
                              <>
                                <button
                                  onClick={() => handleUpdateLeaveStatus(leave.id, 'APPROVED')}
                                  className="text-green-600 hover:text-green-900 mr-4"
                                  disabled={loading}
                                >
                                  Approve
                                </button>
                                <button
                                  onClick={() => handleUpdateLeaveStatus(leave.id, 'DENIED')}
                                  className="text-red-600 hover:text-red-900"
                                  disabled={loading}
                                >
                                  Deny
                                </button>
                              </>
                            )}
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

        {/* Leave Request Form Modal */}
        {isFormOpen && (
          <div className="fixed inset-0 overflow-y-auto z-50 flex items-center justify-center bg-black bg-opacity-50">
            <div className="bg-white rounded-lg shadow-xl max-w-lg w-full mx-4 p-6">
              <div className="flex justify-between items-center mb-6">
                <h2 className="text-xl font-bold">Create Leave Request</h2>
                <button
                  onClick={handleCloseForm}
                  className="text-gray-400 hover:text-gray-500"
                >
                  <svg className="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M6 18L18 6M6 6l12 12" />
                  </svg>
                </button>
              </div>
              <LeaveForm
                employees={employees}
                onSubmit={handleSubmitLeaveForm}
                onCancel={handleCloseForm}
              />
            </div>
          </div>
        )}
      </Layout>
    </ProtectedRoute>
  );
};

export default LeavePage;