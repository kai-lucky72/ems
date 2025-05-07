import { useState, useEffect } from 'react';
import { NextPage } from 'next';
import dynamic from 'next/dynamic';
import Layout from '@/components/Layout';
import ProtectedRoute from '@/components/auth/ProtectedRoute';
import { fetchAnalytics } from '@/lib/api';
import { AnalyticsData } from '@/types';

// Dynamically import the Chart component to prevent SSR issues
const AnalyticChart = dynamic(() => import('@/components/AnalyticChart'), {
  ssr: false,
  loading: () => <div className="h-64 flex items-center justify-center">Loading chart...</div>,
});

interface AnalyticsPageProps {}

const AnalyticsPage: NextPage<AnalyticsPageProps> = () => {
  const [analyticsData, setAnalyticsData] = useState<AnalyticsData | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [selectedTimeframe, setSelectedTimeframe] = useState<'month' | 'quarter' | 'year'>('month');
  const [selectedDepartment, setSelectedDepartment] = useState<string>('all');

  useEffect(() => {
    const loadData = async () => {
      try {
        setLoading(true);
        const data = await fetchAnalytics();
        setAnalyticsData(data);
        setError('');
      } catch (err: any) {
        console.error('Failed to fetch analytics data:', err);
        setError('Failed to load analytics data. Please try again later.');
      } finally {
        setLoading(false);
      }
    };

    loadData();
  }, []);

  if (loading || !analyticsData) {
    return (
      <ProtectedRoute requiredRole="MANAGER">
        <Layout>
          <div className="flex justify-center items-center min-h-[60vh]">
            <div className="animate-spin rounded-full h-12 w-12 border-t-2 border-b-2 border-primary"></div>
          </div>
        </Layout>
      </ProtectedRoute>
    );
  }

  return (
    <ProtectedRoute requiredRole="MANAGER">
      <Layout>
        <div className="space-y-8">
          <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between">
            <h1 className="text-2xl font-bold">Analytics Dashboard</h1>
            <div className="flex space-x-2 mt-3 sm:mt-0">
              <select
                value={selectedTimeframe}
                onChange={(e) => setSelectedTimeframe(e.target.value as 'month' | 'quarter' | 'year')}
                className="input text-sm w-32"
              >
                <option value="month">This Month</option>
                <option value="quarter">This Quarter</option>
                <option value="year">This Year</option>
              </select>
              <select
                value={selectedDepartment}
                onChange={(e) => setSelectedDepartment(e.target.value)}
                className="input text-sm w-40"
              >
                <option value="all">All Departments</option>
                {analyticsData.departmentBudget.labels.map((dept) => (
                  <option key={dept} value={dept}>
                    {dept}
                  </option>
                ))}
              </select>
            </div>
          </div>

          {error && (
            <div className="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded">
              {error}
            </div>
          )}

          {/* KPI Summary Cards */}
          <div className="grid grid-cols-1 gap-6 sm:grid-cols-2 lg:grid-cols-4">
            <div className="bg-white overflow-hidden shadow rounded-lg">
              <div className="px-4 py-5 sm:p-6">
                <div className="flex items-center">
                  <div className="flex-shrink-0 bg-blue-100 rounded-md p-3">
                    <svg className="h-6 w-6 text-blue-600" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                      <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.656.126-1.283.356-1.857m0 0a5.002 5.002 0 019.288 0M15 7a3 3 0 11-6 0 3 3 0 016 0zm6 3a2 2 0 11-4 0 2 2 0 014 0zM7 10a2 2 0 11-4 0 2 2 0 014 0z" />
                    </svg>
                  </div>
                  <div className="ml-5 w-0 flex-1">
                    <dl>
                      <dt className="text-sm font-medium text-gray-500 truncate">Total Employees</dt>
                      <dd className="flex items-baseline">
                        <div className="text-2xl font-semibold text-gray-900">
                          {analyticsData.employeeDistribution.counts.reduce((sum, count) => sum + count, 0)}
                        </div>
                      </dd>
                    </dl>
                  </div>
                </div>
              </div>
            </div>

            <div className="bg-white overflow-hidden shadow rounded-lg">
              <div className="px-4 py-5 sm:p-6">
                <div className="flex items-center">
                  <div className="flex-shrink-0 bg-green-100 rounded-md p-3">
                    <svg className="h-6 w-6 text-green-600" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                      <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 8c-1.657 0-3 .895-3 2s1.343 2 3 2 3 .895 3 2-1.343 2-3 2m0-8c1.11 0 2.08.402 2.599 1M12 8V7m0 1v8m0 0v1m0-1c-1.11 0-2.08-.402-2.599-1M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
                    </svg>
                  </div>
                  <div className="ml-5 w-0 flex-1">
                    <dl>
                      <dt className="text-sm font-medium text-gray-500 truncate">Salary Expenses</dt>
                      <dd className="flex items-baseline">
                        <div className="text-2xl font-semibold text-gray-900">
                          ${analyticsData.salaryData.totalGross.toLocaleString('en-US', { maximumFractionDigits: 0 })}
                        </div>
                      </dd>
                    </dl>
                  </div>
                </div>
              </div>
            </div>

            <div className="bg-white overflow-hidden shadow rounded-lg">
              <div className="px-4 py-5 sm:p-6">
                <div className="flex items-center">
                  <div className="flex-shrink-0 bg-indigo-100 rounded-md p-3">
                    <svg className="h-6 w-6 text-indigo-600" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                      <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 19v-6a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2a2 2 0 002-2zm0 0V9a2 2 0 012-2h2a2 2 0 012 2v10m-6 0a2 2 0 002 2h2a2 2 0 002-2m0 0V5a2 2 0 012-2h2a2 2 0 012 2v14a2 2 0 01-2 2h-2a2 2 0 01-2-2z" />
                    </svg>
                  </div>
                  <div className="ml-5 w-0 flex-1">
                    <dl>
                      <dt className="text-sm font-medium text-gray-500 truncate">Average Salary</dt>
                      <dd className="flex items-baseline">
                        <div className="text-2xl font-semibold text-gray-900">
                          ${analyticsData.salaryData.averageSalary.toLocaleString('en-US', { maximumFractionDigits: 0 })}
                        </div>
                      </dd>
                    </dl>
                  </div>
                </div>
              </div>
            </div>

            <div className="bg-white overflow-hidden shadow rounded-lg">
              <div className="px-4 py-5 sm:p-6">
                <div className="flex items-center">
                  <div className="flex-shrink-0 bg-yellow-100 rounded-md p-3">
                    <svg className="h-6 w-6 text-yellow-600" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                      <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z" />
                    </svg>
                  </div>
                  <div className="ml-5 w-0 flex-1">
                    <dl>
                      <dt className="text-sm font-medium text-gray-500 truncate">Pending Leaves</dt>
                      <dd className="flex items-baseline">
                        <div className="text-2xl font-semibold text-gray-900">
                          {analyticsData.leaveStatus.labels.includes('PENDING') ? 
                            analyticsData.leaveStatus.counts[analyticsData.leaveStatus.labels.indexOf('PENDING')] : 0}
                        </div>
                      </dd>
                    </dl>
                  </div>
                </div>
              </div>
            </div>
          </div>

          {/* Charts Row 1 */}
          <div className="grid grid-cols-1 gap-6 lg:grid-cols-2">
            {/* Department Budget vs. Actual */}
            <div className="bg-white shadow rounded-lg p-6">
              <h2 className="text-lg font-medium text-gray-900 mb-4">Department Budget vs. Actual</h2>
              <div className="h-80">
                <AnalyticChart
                  type="bar"
                  data={{
                    labels: analyticsData.departmentBudget.labels,
                    datasets: [
                      {
                        label: 'Budget',
                        data: analyticsData.departmentBudget.budget,
                        backgroundColor: 'rgba(99, 102, 241, 0.5)',
                        borderColor: 'rgb(99, 102, 241)',
                      },
                      {
                        label: 'Actual',
                        data: analyticsData.departmentBudget.actual,
                        backgroundColor: 'rgba(52, 211, 153, 0.5)',
                        borderColor: 'rgb(52, 211, 153)',
                      },
                    ],
                  }}
                />
              </div>
            </div>

            {/* Salary Distribution by Department */}
            <div className="bg-white shadow rounded-lg p-6">
              <h2 className="text-lg font-medium text-gray-900 mb-4">Salary Distribution by Department</h2>
              <div className="h-80">
                <AnalyticChart
                  type="pie"
                  data={{
                    labels: analyticsData.salaryData.departmentSalaries.map(ds => ds.department),
                    datasets: [
                      {
                        data: analyticsData.salaryData.departmentSalaries.map(ds => ds.totalSalary),
                        backgroundColor: [
                          'rgba(99, 102, 241, 0.7)',
                          'rgba(52, 211, 153, 0.7)',
                          'rgba(251, 191, 36, 0.7)',
                          'rgba(239, 68, 68, 0.7)',
                          'rgba(16, 185, 129, 0.7)',
                          'rgba(245, 158, 11, 0.7)',
                          'rgba(139, 92, 246, 0.7)',
                        ],
                      },
                    ],
                  }}
                />
              </div>
            </div>
          </div>

          {/* Charts Row 2 */}
          <div className="grid grid-cols-1 gap-6 lg:grid-cols-2">
            {/* Employee Distribution */}
            <div className="bg-white shadow rounded-lg p-6">
              <h2 className="text-lg font-medium text-gray-900 mb-4">Employee Distribution by Department</h2>
              <div className="h-80">
                <AnalyticChart
                  type="doughnut"
                  data={{
                    labels: analyticsData.employeeDistribution.labels,
                    datasets: [
                      {
                        data: analyticsData.employeeDistribution.counts,
                        backgroundColor: [
                          'rgba(99, 102, 241, 0.7)',
                          'rgba(52, 211, 153, 0.7)',
                          'rgba(251, 191, 36, 0.7)',
                          'rgba(239, 68, 68, 0.7)',
                          'rgba(16, 185, 129, 0.7)',
                          'rgba(245, 158, 11, 0.7)',
                          'rgba(139, 92, 246, 0.7)',
                        ],
                      },
                    ],
                  }}
                />
              </div>
            </div>

            {/* Leave Status */}
            <div className="bg-white shadow rounded-lg p-6">
              <h2 className="text-lg font-medium text-gray-900 mb-4">Leave Status Distribution</h2>
              <div className="h-80">
                <AnalyticChart
                  type="polarArea"
                  data={{
                    labels: analyticsData.leaveStatus.labels,
                    datasets: [
                      {
                        data: analyticsData.leaveStatus.counts,
                        backgroundColor: [
                          'rgba(251, 191, 36, 0.7)',
                          'rgba(52, 211, 153, 0.7)',
                          'rgba(239, 68, 68, 0.7)',
                        ],
                      },
                    ],
                  }}
                />
              </div>
            </div>
          </div>

          {/* Charts Row 3 */}
          <div className="grid grid-cols-1 gap-6 lg:grid-cols-2">
            {/* Employee Activity Timeline */}
            <div className="bg-white shadow rounded-lg p-6">
              <h2 className="text-lg font-medium text-gray-900 mb-4">Employee Activity Timeline</h2>
              <div className="h-80">
                <AnalyticChart
                  type="line"
                  data={{
                    labels: analyticsData.employeeTimeline.months,
                    datasets: [
                      {
                        label: 'Active Employees',
                        data: analyticsData.employeeTimeline.active,
                        borderColor: 'rgb(52, 211, 153)',
                        backgroundColor: 'rgba(52, 211, 153, 0.1)',
                        tension: 0.4,
                      },
                      {
                        label: 'Inactive Employees',
                        data: analyticsData.employeeTimeline.inactive,
                        borderColor: 'rgb(239, 68, 68)',
                        backgroundColor: 'rgba(239, 68, 68, 0.1)',
                        tension: 0.4,
                      },
                    ],
                  }}
                />
              </div>
            </div>

            {/* Contract Type & Role Distribution */}
            <div className="grid grid-cols-1 gap-6">
              {/* Contract Type Distribution */}
              <div className="bg-white shadow rounded-lg p-6">
                <h2 className="text-lg font-medium text-gray-900 mb-4">Contract Type Distribution</h2>
                <div className="h-40">
                  <AnalyticChart
                    type="bar"
                    data={{
                      labels: analyticsData.contractTypeDistribution.labels.map(label => label.replace('_', ' ')),
                      datasets: [
                        {
                          data: analyticsData.contractTypeDistribution.counts,
                          backgroundColor: [
                            'rgba(99, 102, 241, 0.7)',
                            'rgba(52, 211, 153, 0.7)',
                            'rgba(251, 191, 36, 0.7)',
                          ],
                        },
                      ],
                    }}
                  />
                </div>
              </div>

              {/* Role Distribution */}
              <div className="bg-white shadow rounded-lg p-6">
                <h2 className="text-lg font-medium text-gray-900 mb-4">Role Distribution</h2>
                <div className="h-40">
                  <AnalyticChart
                    type="bar"
                    data={{
                      labels: analyticsData.roleDistribution.labels,
                      datasets: [
                        {
                          data: analyticsData.roleDistribution.counts,
                          backgroundColor: [
                            'rgba(139, 92, 246, 0.7)',
                            'rgba(16, 185, 129, 0.7)',
                            'rgba(245, 158, 11, 0.7)',
                            'rgba(239, 68, 68, 0.7)',
                          ],
                        },
                      ],
                    }}
                  />
                </div>
              </div>
            </div>
          </div>
        </div>
      </Layout>
    </ProtectedRoute>
  );
};

export default AnalyticsPage;