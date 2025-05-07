import { useState, useEffect } from 'react';
import { NextPage } from 'next';
import Layout from '@/components/Layout';
import ProtectedRoute from '@/components/auth/ProtectedRoute';
import { getUser } from '@/lib/auth';
import { Leave } from '@/types';

const MyLeavePage: NextPage = () => {
  const [leaves, setLeaves] = useState<Leave[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [showRequestForm, setShowRequestForm] = useState(false);
  const [formData, setFormData] = useState({
    startDate: '',
    endDate: '',
    reason: '',
  });

  // Get mock data for now - in a real app, this would come from the API
  useEffect(() => {
    // Simulate API call
    setTimeout(() => {
      try {
        // Mock leave data
        const mockLeaves: Leave[] = [
          {
            id: 1,
            employeeId: 1,
            employeeName: 'John Doe',
            startDate: '2023-05-15',
            endDate: '2023-05-20',
            reason: 'Family vacation',
            status: 'APPROVED',
            createdAt: '2023-04-20T10:30:00Z',
          },
          {
            id: 2,
            employeeId: 1,
            employeeName: 'John Doe',
            startDate: '2023-07-10',
            endDate: '2023-07-12',
            reason: 'Medical appointment',
            status: 'PENDING',
            createdAt: '2023-06-25T14:15:00Z',
          },
          {
            id: 3,
            employeeId: 1,
            employeeName: 'John Doe',
            startDate: '2023-03-05',
            endDate: '2023-03-07',
            reason: 'Personal leave',
            status: 'DENIED',
            createdAt: '2023-02-20T09:45:00Z',
          },
        ];
        
        setLeaves(mockLeaves);
        setLoading(false);
      } catch (err) {
        console.error('Error fetching leave data:', err);
        setError('Failed to load leave data. Please try again later.');
        setLoading(false);
      }
    }, 1000);
  }, []);

  const handleInputChange = (
    e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>
  ) => {
    const { name, value } = e.target;
    setFormData({
      ...formData,
      [name]: value,
    });
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    
    try {
      // Here we would make an API call to submit the leave request
      // For now, we'll just simulate a successful request
      
      setTimeout(() => {
        const currentUser = getUser();
        const newLeave: Leave = {
          id: Math.floor(Math.random() * 1000) + 4,
          employeeId: currentUser?.id || 1,
          employeeName: currentUser?.fullName || 'John Doe',
          startDate: formData.startDate,
          endDate: formData.endDate,
          reason: formData.reason,
          status: 'PENDING',
          createdAt: new Date().toISOString(),
        };
        
        setLeaves([newLeave, ...leaves]);
        setShowRequestForm(false);
        setFormData({
          startDate: '',
          endDate: '',
          reason: '',
        });
        setLoading(false);
      }, 1000);
    } catch (error) {
      console.error('Failed to submit leave request:', error);
      setError('Failed to submit leave request. Please try again later.');
      setLoading(false);
    }
  };

  // Function to get status badge class based on leave status
  const getStatusBadgeClass = (status: string) => {
    switch (status) {
      case 'APPROVED':
        return 'badge badge-success';
      case 'DENIED':
        return 'badge badge-danger';
      case 'PENDING':
      default:
        return 'badge badge-warning';
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

  return (
    <ProtectedRoute>
      <Layout>
        <div className="space-y-6">
          {/* Page header */}
          <div className="flex items-center justify-between">
            <h1 className="text-2xl font-bold">My Leave Requests</h1>
            <button
              onClick={() => setShowRequestForm(!showRequestForm)}
              className="btn btn-primary"
            >
              {showRequestForm ? 'Cancel' : 'Request Leave'}
            </button>
          </div>
          
          {/* Leave request form */}
          {showRequestForm && (
            <div className="bg-white rounded-lg shadow p-6">
              <h2 className="text-lg font-semibold mb-4">New Leave Request</h2>
              <form onSubmit={handleSubmit} className="space-y-4">
                <div className="grid grid-cols-1 gap-4 sm:grid-cols-2">
                  <div>
                    <label htmlFor="startDate" className="label">
                      Start Date
                    </label>
                    <input
                      type="date"
                      id="startDate"
                      name="startDate"
                      value={formData.startDate}
                      onChange={handleInputChange}
                      className="input"
                      required
                      min={new Date().toISOString().split('T')[0]}
                    />
                  </div>
                  <div>
                    <label htmlFor="endDate" className="label">
                      End Date
                    </label>
                    <input
                      type="date"
                      id="endDate"
                      name="endDate"
                      value={formData.endDate}
                      onChange={handleInputChange}
                      className="input"
                      required
                      min={formData.startDate || new Date().toISOString().split('T')[0]}
                    />
                  </div>
                </div>
                <div>
                  <label htmlFor="reason" className="label">
                    Reason
                  </label>
                  <textarea
                    id="reason"
                    name="reason"
                    rows={3}
                    value={formData.reason}
                    onChange={handleInputChange}
                    className="input"
                    required
                  />
                </div>
                <div className="flex justify-end">
                  <button
                    type="submit"
                    disabled={loading}
                    className="btn btn-primary"
                  >
                    {loading ? 'Submitting...' : 'Submit Request'}
                  </button>
                </div>
              </form>
            </div>
          )}
          
          {/* Error message */}
          {error && (
            <div className="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded">
              {error}
            </div>
          )}
          
          {/* Loading spinner */}
          {loading && !showRequestForm && (
            <div className="flex justify-center py-12">
              <div className="animate-spin rounded-full h-12 w-12 border-t-2 border-b-2 border-primary"></div>
            </div>
          )}
          
          {/* Leave history */}
          {!loading && leaves.length === 0 ? (
            <div className="bg-white rounded-lg shadow p-6 text-center">
              <p className="text-gray-500">You have no leave requests yet.</p>
            </div>
          ) : (
            <div className="bg-white rounded-lg shadow overflow-hidden">
              <div className="px-4 py-5 sm:px-6">
                <h2 className="text-lg font-semibold">Leave History</h2>
                <p className="text-sm text-gray-500">Your leave requests and their status.</p>
              </div>
              <div className="border-t border-gray-200">
                <div className="overflow-x-auto">
                  <table className="min-w-full divide-y divide-gray-200">
                    <thead className="bg-gray-50">
                      <tr>
                        <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                          Date Requested
                        </th>
                        <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                          Period
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
                      </tr>
                    </thead>
                    <tbody className="bg-white divide-y divide-gray-200">
                      {leaves.map((leave) => {
                        // Calculate duration in days
                        const start = new Date(leave.startDate);
                        const end = new Date(leave.endDate);
                        const diffTime = Math.abs(end.getTime() - start.getTime());
                        const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24)) + 1;
                        
                        return (
                          <tr key={leave.id}>
                            <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                              {formatDate(leave.createdAt)}
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
                            <td className="px-6 py-4 whitespace-nowrap">
                              <span className={getStatusBadgeClass(leave.status)}>
                                {leave.status}
                              </span>
                            </td>
                          </tr>
                        );
                      })}
                    </tbody>
                  </table>
                </div>
              </div>
            </div>
          )}
        </div>
      </Layout>
    </ProtectedRoute>
  );
};

export default MyLeavePage;