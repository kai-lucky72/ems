import { useState, useEffect } from 'react';
import Head from 'next/head';
import { 
  fetchEmployees, 
  fetchLeaves, 
  createLeave, 
  updateLeaveStatus 
} from '../../lib/api';
import LeaveForm from '../../components/LeaveForm';

interface Employee {
  id: number;
  name: string;
  departmentName: string;
  isActive: boolean;
}

interface Leave {
  id: number;
  employeeId: number;
  employeeName: string;
  startDate: string;
  endDate: string;
  reason: string;
  status: 'PENDING' | 'APPROVED' | 'DENIED';
  createdAt: string;
}

const LeavePage = () => {
  const [leaves, setLeaves] = useState<Leave[]>([]);
  const [employees, setEmployees] = useState<Employee[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [isFormOpen, setIsFormOpen] = useState(false);
  const [filterStatus, setFilterStatus] = useState<string>('ALL');
  const [filterEmployee, setFilterEmployee] = useState<number>(0);

  const loadData = async () => {
    try {
      setLoading(true);
      const [employeesData, leavesData] = await Promise.all([
        fetchEmployees(),
        fetchLeaves()
      ]);
      setEmployees(employeesData);
      setLeaves(leavesData);
      setError('');
    } catch (err: any) {
      setError(err.message || 'Failed to load data');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadData();
  }, []);

  const handleOpenForm = () => {
    setIsFormOpen(true);
  };

  const handleCloseForm = () => {
    setIsFormOpen(false);
  };

  const handleSubmit = async (formData: { 
    employeeId: number;
    startDate: string;
    endDate: string;
    reason: string;
  }) => {
    try {
      await createLeave(formData);
      handleCloseForm();
      await loadData();
    } catch (err: any) {
      setError(err.message || 'Failed to save leave request');
    }
  };

  const handleStatusChange = async (leaveId: number, status: 'APPROVED' | 'DENIED') => {
    try {
      await updateLeaveStatus(leaveId, { status });
      await loadData();
    } catch (err: any) {
      setError(err.message || 'Failed to update leave status');
    }
  };

  const formatDate = (dateString: string) => {
    return new Date(dateString).toLocaleDateString();
  };

  // Apply filters
  const filteredLeaves = leaves.filter(leave => {
    if (filterStatus !== 'ALL' && leave.status !== filterStatus) {
      return false;
    }
    if (filterEmployee !== 0 && leave.employeeId !== filterEmployee) {
      return false;
    }
    return true;
  });

  const getStatusClass = (status: string) => {
    switch (status) {
      case 'APPROVED': return 'bg-green-100 text-green-800 border-green-300';
      case 'DENIED': return 'bg-red-100 text-red-800 border-red-300';
      default: return 'bg-yellow-100 text-yellow-800 border-yellow-300';
    }
  };

  return (
    <div className="min-h-screen bg-gray-50">
      <Head>
        <title>Leave Management | Employee Management System</title>
      </Head>

      <div className="py-6">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 md:px-8 flex justify-between items-center">
          <h1 className="text-2xl font-semibold text-gray-900">Leave Management</h1>
          <button
            onClick={handleOpenForm}
            className="inline-flex items-center px-4 py-2 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-blue-600 hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500"
          >
            Add Leave Request
          </button>
        </div>

        <div className="max-w-7xl mx-auto px-4 sm:px-6 md:px-8 mt-6">
          {error && (
            <div className="mb-4 p-4 bg-red-100 border-l-4 border-red-500 text-red-700">
              {error}
            </div>
          )}

          {/* Filters */}
          <div className="bg-white p-4 rounded-lg shadow mb-6 flex flex-wrap gap-4 items-center">
            <div>
              <label htmlFor="status-filter" className="block text-sm font-medium text-gray-700 mb-1">
                Status
              </label>
              <select
                id="status-filter"
                value={filterStatus}
                onChange={(e) => setFilterStatus(e.target.value)}
                className="mt-1 block w-full pl-3 pr-10 py-2 text-base border-gray-300 focus:outline-none focus:ring-blue-500 focus:border-blue-500 sm:text-sm rounded-md"
              >
                <option value="ALL">All Statuses</option>
                <option value="PENDING">Pending</option>
                <option value="APPROVED">Approved</option>
                <option value="DENIED">Denied</option>
              </select>
            </div>
            <div>
              <label htmlFor="employee-filter" className="block text-sm font-medium text-gray-700 mb-1">
                Employee
              </label>
              <select
                id="employee-filter"
                value={filterEmployee}
                onChange={(e) => setFilterEmployee(Number(e.target.value))}
                className="mt-1 block w-full pl-3 pr-10 py-2 text-base border-gray-300 focus:outline-none focus:ring-blue-500 focus:border-blue-500 sm:text-sm rounded-md"
              >
                <option value="0">All Employees</option>
                {employees.map(employee => (
                  <option key={employee.id} value={employee.id}>
                    {employee.name}
                  </option>
                ))}
              </select>
            </div>
          </div>

          {loading ? (
            <div className="flex justify-center items-center h-64">
              <div className="h-8 w-8 animate-spin rounded-full border-b-2 border-t-2 border-blue-500"></div>
            </div>
          ) : filteredLeaves.length === 0 ? (
            <div className="bg-white shadow overflow-hidden sm:rounded-md">
              <div className="px-4 py-5 sm:p-6 text-center">
                <p className="text-gray-500">No leave requests found.</p>
              </div>
            </div>
          ) : (
            <div className="bg-white shadow overflow-hidden sm:rounded-md">
              <ul className="divide-y divide-gray-200">
                {filteredLeaves.map((leave) => (
                  <li key={leave.id}>
                    <div className="px-4 py-4 sm:px-6">
                      <div className="flex items-center justify-between">
                        <div className="flex-1">
                          <div className="flex items-center">
                            <h3 className="text-lg font-medium text-gray-900 mr-3">
                              {leave.employeeName}
                            </h3>
                            <span className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium border ${getStatusClass(leave.status)}`}>
                              {leave.status}
                            </span>
                          </div>
                          <div className="mt-2 flex flex-col sm:flex-row sm:flex-wrap sm:mt-0 sm:space-x-6">
                            <div className="mt-2 flex items-center text-sm text-gray-500 sm:mt-0">
                              <span className="mr-1">Period:</span>
                              <span className="font-medium">
                                {formatDate(leave.startDate)} - {formatDate(leave.endDate)}
                              </span>
                            </div>
                            <div className="mt-2 flex items-center text-sm text-gray-500 sm:mt-0">
                              <span className="mr-1">Requested on:</span>
                              <span className="font-medium">{formatDate(leave.createdAt)}</span>
                            </div>
                          </div>

                          {/* Reason */}
                          <div className="mt-3">
                            <h4 className="text-sm font-medium text-gray-700">Reason:</h4>
                            <p className="mt-1 text-sm text-gray-500">{leave.reason}</p>
                          </div>
                        </div>
                        
                        {leave.status === 'PENDING' && (
                          <div className="ml-4 flex-shrink-0 flex">
                            <button
                              onClick={() => handleStatusChange(leave.id, 'APPROVED')}
                              className="mr-2 inline-flex items-center px-3 py-1 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-green-600 hover:bg-green-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-green-500"
                            >
                              Approve
                            </button>
                            <button
                              onClick={() => handleStatusChange(leave.id, 'DENIED')}
                              className="inline-flex items-center px-3 py-1 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-red-600 hover:bg-red-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-red-500"
                            >
                              Deny
                            </button>
                          </div>
                        )}
                      </div>
                    </div>
                  </li>
                ))}
              </ul>
            </div>
          )}
        </div>
      </div>

      {isFormOpen && (
        <LeaveForm
          employees={employees.filter(emp => emp.isActive)}
          onSubmit={handleSubmit}
          onCancel={handleCloseForm}
        />
      )}
    </div>
  );
};

export default LeavePage;
