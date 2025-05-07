import { useEffect, useState } from 'react';
import { NextPage } from 'next';
import Layout from '@/components/Layout';
import { fetchAnalytics } from '@/lib/api';
import { AnalyticsData } from '@/types';

const Dashboard: NextPage = () => {
  const [loading, setLoading] = useState(true);
  const [analytics, setAnalytics] = useState<AnalyticsData | null>(null);
  const [error, setError] = useState('');

  useEffect(() => {
    const loadAnalytics = async () => {
      try {
        setLoading(true);
        const data = await fetchAnalytics();
        setAnalytics(data);
        setError('');
      } catch (err: any) {
        console.error('Failed to fetch analytics:', err);
        setError('Failed to load dashboard data. Please try again later.');
      } finally {
        setLoading(false);
      }
    };

    loadAnalytics();
  }, []);

  return (
    <Layout>
      <div className="space-y-6">
        <div className="flex items-center justify-between">
          <h1 className="text-2xl font-bold">Dashboard Overview</h1>
        </div>

        {error ? (
          <div className="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded">
            {error}
          </div>
        ) : loading ? (
          <div className="flex justify-center py-12">
            <div className="animate-spin rounded-full h-12 w-12 border-t-2 border-b-2 border-primary"></div>
          </div>
        ) : (
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
            {/* Summary Cards */}
            <div className="bg-white rounded-lg shadow p-6">
              <h2 className="text-lg font-semibold mb-4">Employees</h2>
              <div className="flex items-center">
                <div className="p-3 rounded-full bg-blue-100 text-blue-600 mr-4">
                  <svg xmlns="http://www.w3.org/2000/svg" className="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 4.354a4 4 0 110 5.292M15 21H3v-1a6 6 0 0112 0v1zm0 0h6v-1a6 6 0 00-9-5.197M13 7a4 4 0 11-8 0 4 4 0 018 0z" />
                  </svg>
                </div>
                <div>
                  <p className="text-sm text-gray-500">Total Employees</p>
                  <p className="text-xl font-bold">
                    {analytics?.employeeDistribution.counts.reduce((a, b) => a + b, 0) || 0}
                  </p>
                </div>
              </div>
              <div className="mt-4 flex justify-between text-sm">
                <div>
                  <p className="text-gray-500">Active</p>
                  <p className="font-medium">{analytics?.employeeDistribution.counts[0] || 0}</p>
                </div>
                <div>
                  <p className="text-gray-500">Inactive</p>
                  <p className="font-medium">{analytics?.employeeDistribution.counts[1] || 0}</p>
                </div>
              </div>
            </div>

            <div className="bg-white rounded-lg shadow p-6">
              <h2 className="text-lg font-semibold mb-4">Payroll</h2>
              <div className="flex items-center">
                <div className="p-3 rounded-full bg-green-100 text-green-600 mr-4">
                  <svg xmlns="http://www.w3.org/2000/svg" className="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 8c-1.657 0-3 .895-3 2s1.343 2 3 2 3 .895 3 2-1.343 2-3 2m0-8c1.11 0 2.08.402 2.599 1M12 8V7m0 1v8m0 0v1m0-1c-1.11 0-2.08-.402-2.599-1M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
                  </svg>
                </div>
                <div>
                  <p className="text-sm text-gray-500">Total Salary</p>
                  <p className="text-xl font-bold">
                    ${analytics?.salaryData.totalGross.toLocaleString() || 0}
                  </p>
                </div>
              </div>
              <div className="mt-4 flex justify-between text-sm">
                <div>
                  <p className="text-gray-500">Net Pay</p>
                  <p className="font-medium">${analytics?.salaryData.totalNet.toLocaleString() || 0}</p>
                </div>
                <div>
                  <p className="text-gray-500">Avg. Salary</p>
                  <p className="font-medium">${analytics?.salaryData.averageSalary.toLocaleString() || 0}</p>
                </div>
              </div>
            </div>

            <div className="bg-white rounded-lg shadow p-6">
              <h2 className="text-lg font-semibold mb-4">Leave Requests</h2>
              <div className="flex items-center">
                <div className="p-3 rounded-full bg-yellow-100 text-yellow-600 mr-4">
                  <svg xmlns="http://www.w3.org/2000/svg" className="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z" />
                  </svg>
                </div>
                <div>
                  <p className="text-sm text-gray-500">Total Leave Requests</p>
                  <p className="text-xl font-bold">
                    {analytics?.leaveStatus.counts.reduce((a, b) => a + b, 0) || 0}
                  </p>
                </div>
              </div>
              <div className="mt-4 flex justify-between text-sm">
                <div>
                  <p className="text-gray-500">Pending</p>
                  <p className="font-medium">{analytics?.leaveStatus.counts[0] || 0}</p>
                </div>
                <div>
                  <p className="text-gray-500">Approved</p>
                  <p className="font-medium">{analytics?.leaveStatus.counts[1] || 0}</p>
                </div>
                <div>
                  <p className="text-gray-500">Denied</p>
                  <p className="font-medium">{analytics?.leaveStatus.counts[2] || 0}</p>
                </div>
              </div>
            </div>
          </div>
        )}

        {!loading && !error && (
          <>
            <div className="grid grid-cols-1 md:grid-cols-2 gap-6 mt-6">
              <div className="bg-white rounded-lg shadow p-6">
                <h2 className="text-lg font-semibold mb-4">Department Budget Overview</h2>
                <div className="h-64 flex items-center justify-center">
                  <p className="text-gray-500 italic">Chart visualization would appear here</p>
                </div>
              </div>

              <div className="bg-white rounded-lg shadow p-6">
                <h2 className="text-lg font-semibold mb-4">Employee Distribution</h2>
                <div className="h-64 flex items-center justify-center">
                  <p className="text-gray-500 italic">Chart visualization would appear here</p>
                </div>
              </div>
            </div>

            <div className="bg-white rounded-lg shadow p-6 mt-6">
              <h2 className="text-lg font-semibold mb-4">Recent Activities</h2>
              <div className="overflow-x-auto">
                <div className="flex items-center justify-center py-6">
                  <p className="text-gray-500 italic">Activity log would appear here</p>
                </div>
              </div>
            </div>
          </>
        )}
      </div>
    </Layout>
  );
};

export default Dashboard;