import { useState, useEffect } from 'react';
import { NextPage } from 'next';
import Layout from '@/components/Layout';
import LeaveForm from '@/components/LeaveForm';
import { fetchEmployees, fetchLeaves, createLeave, updateLeaveStatus } from '@/lib/api';
import { Leave, LeaveFormData, LeaveStatusFormData } from '@/types';

interface Employee {
  id: number;
  name: string;
  departmentName: string;
  isActive: boolean;
}

const LeavePage: NextPage = () => {
  const [leaves, setLeaves] = useState<Leave[]>([]);
  const [employees, setEmployees] = useState<Employee[]>([]);
  const [isFormOpen, setIsFormOpen] = useState(false);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [searchQuery, setSearchQuery] = useState('');
  const [filterStatus, setFilterStatus] = useState('');

  useEffect(() => {
    const loadData = async () => {
      try {
        setLoading(true);
        // Mock data for development until backend is connected
        const mockLeaves: Leave[] = [];
        const mockEmployees: Employee[] = [
          { id: 1, name: 'John Doe', departmentName: 'Engineering', isActive: true },
          { id: 2, name: 'Jane Smith', departmentName: 'Marketing', isActive: true }
        ];
        
        setLeaves(mockLeaves);
        setEmployees(mockEmployees);
        setError('');
      } catch (err: any) {
        console.error('Failed to fetch data:', err);
        setError('Failed to load leave data. Please try again later.');
      } finally {
        setLoading(false);
      }
    };

    loadData();
  }, []);

  const handleAddLeave = () => {
    setIsFormOpen(true);
  };

  const handleCloseForm = () => {
    setIsFormOpen(false);
  };

  const handleSubmitLeaveForm = async (formData: LeaveFormData) => {
    try {
      const newLeave = await createLeave(formData);
      setLeaves(prev => [...prev, newLeave]);
      setIsFormOpen(false);
    } catch (err: any) {
      throw new Error(err.response?.data?.message || 'Failed to create leave request');
    }
  };

  const handleUpdateLeaveStatus = async (id: number, status: 'APPROVED' | 'DENIED') => {
    try {
      const statusData: LeaveStatusFormData = { status };
      const updatedLeave = await updateLeaveStatus(id, statusData);
      setLeaves(prev => prev.map(leave => leave.id === id ? updatedLeave : leave));
    } catch (err: any) {
      console.error('Failed to update leave status:', err);
      setError(err.response?.data?.message || 'Failed to update leave status');
    }
  };

  // Filter leaves based on search query and status filter
  const filteredLeaves = leaves.filter(leave => {
    const matchesSearch = 
      leave.employeeName.toLowerCase().includes(searchQuery.toLowerCase()) || 
      leave.reason.toLowerCase().includes(searchQuery.toLowerCase());
    
    const matchesStatus = filterStatus === '' || leave.status === filterStatus;
    
    return matchesSearch && matchesStatus;
  });

  // Format date to a readable format
  const formatDate = (dateString: string) => {
    const date = new Date(dateString);
    return date.toLocaleDateString('en-US', {
      year: 'numeric',
      month: 'short',
      day: 'numeric'
    });
  };

  // Calculate duration between two dates
  const calculateDuration = (startDate: string, endDate: string) => {
    const start = new Date(startDate);
    const end = new Date(endDate);
    const diffTime = Math.abs(end.getTime() - start.getTime());
    const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24)) + 1; // Include both start and end day
    return diffDays;
  };

  // Get status badge class
  const getStatusBadgeClass = (status: string) => {
    switch (status) {
      case 'APPROVED':
        return 'bg-green-100 text-green-800';
      case 'DENIED':
        return 'bg-red-100 text-red-800';
      default:
        return 'bg-yellow-100 text-yellow-800';
    }
  };

  return (
    <Layout>
      <div className="space-y-6">
        <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between">
          <h1 className="text-2xl font-bold">Leave Management</h1>
          <button
            onClick={handleAddLeave}
            className="mt-3 sm:mt-0 btn btn-primary"
          >
            Request Leave
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
                  placeholder="Search by employee or reason..."
                  value={searchQuery}
                  onChange={(e) => setSearchQuery(e.target.value)}
                  className="input"
                />
              </div>
              <div className="w-full md:w-64">
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

          {loading ? (
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
                      Status
                    </th>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                      Actions
                    </th>
                  </tr>
                </thead>
                <tbody className="bg-white divide-y divide-gray-200">
                  {filteredLeaves.map((leave) => (
                    <tr key={leave.id}>
                      <td className="px-6 py-4 whitespace-nowrap">
                        <div className="text-sm font-medium text-gray-900">{leave.employeeName}</div>
                        <div className="text-xs text-gray-500">{formatDate(leave.createdAt)}</div>
                      </td>
                      <td className="px-6 py-4 whitespace-nowrap">
                        <div className="text-sm text-gray-900">
                          {formatDate(leave.startDate)} - {formatDate(leave.endDate)}
                        </div>
                      </td>
                      <td className="px-6 py-4 whitespace-nowrap">
                        <div className="text-sm text-gray-900">
                          {calculateDuration(leave.startDate, leave.endDate)} days
                        </div>
                      </td>
                      <td className="px-6 py-4">
                        <div className="text-sm text-gray-900 max-w-xs line-clamp-2">
                          {leave.reason}
                        </div>
                      </td>
                      <td className="px-6 py-4 whitespace-nowrap">
                        <span className={`px-2 inline-flex text-xs leading-5 font-semibold rounded-full ${getStatusBadgeClass(leave.status)}`}>
                          {leave.status}
                        </span>
                      </td>
                      <td className="px-6 py-4 whitespace-nowrap">
                        {leave.status === 'PENDING' && (
                          <div className="flex space-x-2">
                            <button
                              onClick={() => handleUpdateLeaveStatus(leave.id, 'APPROVED')}
                              className="text-green-600 hover:text-green-900"
                            >
                              Approve
                            </button>
                            <button
                              onClick={() => handleUpdateLeaveStatus(leave.id, 'DENIED')}
                              className="text-red-600 hover:text-red-900"
                            >
                              Deny
                            </button>
                          </div>
                        )}
                      </td>
                    </tr>
                  ))}
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
              <h2 className="text-xl font-bold">Request Leave</h2>
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
  );
};

export default LeavePage;