import { useState, useEffect } from 'react';
import Head from 'next/head';
import { fetchAnalytics } from '../../lib/api';
import AnalyticChart from '../../components/AnalyticChart';

interface AnalyticsData {
  departmentBudget: {
    labels: string[];
    actual: number[];
    budget: number[];
  };
  salaryData: {
    totalGross: number;
    totalNet: number;
    averageSalary: number;
    departmentSalaries: {
      department: string;
      totalSalary: number;
    }[];
  };
  employeeDistribution: {
    labels: string[];
    counts: number[];
  };
  leaveStatus: {
    labels: string[];
    counts: number[];
  };
  roleDistribution: {
    labels: string[];
    counts: number[];
  };
  contractTypeDistribution: {
    labels: string[];
    counts: number[];
  };
  employeeTimeline: {
    months: string[];
    active: number[];
    inactive: number[];
  };
}

const AnalyticsPage = () => {
  const [analytics, setAnalytics] = useState<AnalyticsData | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    const loadAnalytics = async () => {
      try {
        setLoading(true);
        const data = await fetchAnalytics();
        setAnalytics(data);
      } catch (err: any) {
        setError(err.message || 'Failed to load analytics data');
      } finally {
        setLoading(false);
      }
    };

    loadAnalytics();
  }, []);

  return (
    <div className="min-h-screen bg-gray-50">
      <Head>
        <title>Analytics | Employee Management System</title>
      </Head>

      <div className="py-6">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 md:px-8">
          <h1 className="text-2xl font-semibold text-gray-900">Analytics Dashboard</h1>
        </div>

        <div className="max-w-7xl mx-auto px-4 sm:px-6 md:px-8">
          {error && (
            <div className="mt-6 bg-red-100 border-l-4 border-red-500 text-red-700 p-4">
              <p>{error}</p>
            </div>
          )}

          {loading ? (
            <div className="flex justify-center items-center h-64">
              <div className="h-8 w-8 animate-spin rounded-full border-b-2 border-t-2 border-blue-500"></div>
            </div>
          ) : analytics ? (
            <div className="mt-6">
              {/* Summary Cards */}
              <div className="grid grid-cols-1 md:grid-cols-3 gap-6 mb-6">
                <div className="bg-white rounded-lg shadow p-6">
                  <h3 className="text-lg font-medium text-gray-900">Total Gross Salary</h3>
                  <p className="text-3xl font-bold text-blue-600 mt-2">
                    ${analytics.salaryData.totalGross.toLocaleString()}
                  </p>
                </div>
                <div className="bg-white rounded-lg shadow p-6">
                  <h3 className="text-lg font-medium text-gray-900">Total Net Salary</h3>
                  <p className="text-3xl font-bold text-green-600 mt-2">
                    ${analytics.salaryData.totalNet.toLocaleString()}
                  </p>
                </div>
                <div className="bg-white rounded-lg shadow p-6">
                  <h3 className="text-lg font-medium text-gray-900">Average Salary</h3>
                  <p className="text-3xl font-bold text-indigo-600 mt-2">
                    ${analytics.salaryData.averageSalary.toLocaleString()}
                  </p>
                </div>
              </div>

              {/* Main Charts */}
              <div className="grid grid-cols-1 lg:grid-cols-2 gap-6 mb-6">
                {/* Department Budget Usage */}
                <div className="bg-white rounded-lg shadow p-6">
                  <h3 className="text-lg font-medium text-gray-900 mb-4">Department Budget Usage</h3>
                  <AnalyticChart
                    type="bar"
                    data={{
                      labels: analytics.departmentBudget.labels,
                      datasets: [
                        {
                          label: 'Actual',
                          data: analytics.departmentBudget.actual,
                          backgroundColor: 'rgba(59, 130, 246, 0.5)',
                        },
                        {
                          label: 'Budget',
                          data: analytics.departmentBudget.budget,
                          backgroundColor: 'rgba(107, 114, 128, 0.5)',
                        },
                      ],
                    }}
                  />
                </div>

                {/* Department Salary Distribution */}
                <div className="bg-white rounded-lg shadow p-6">
                  <h3 className="text-lg font-medium text-gray-900 mb-4">Department Salary Distribution</h3>
                  <AnalyticChart
                    type="pie"
                    data={{
                      labels: analytics.salaryData.departmentSalaries.map(d => d.department),
                      datasets: [
                        {
                          data: analytics.salaryData.departmentSalaries.map(d => d.totalSalary),
                          backgroundColor: [
                            'rgba(59, 130, 246, 0.7)',
                            'rgba(16, 185, 129, 0.7)',
                            'rgba(245, 158, 11, 0.7)',
                            'rgba(239, 68, 68, 0.7)',
                            'rgba(124, 58, 237, 0.7)',
                          ],
                        },
                      ],
                    }}
                  />
                </div>

                {/* Employee Status */}
                <div className="bg-white rounded-lg shadow p-6">
                  <h3 className="text-lg font-medium text-gray-900 mb-4">Employee Status</h3>
                  <AnalyticChart
                    type="doughnut"
                    data={{
                      labels: analytics.employeeDistribution.labels,
                      datasets: [
                        {
                          data: analytics.employeeDistribution.counts,
                          backgroundColor: [
                            'rgba(16, 185, 129, 0.7)',
                            'rgba(239, 68, 68, 0.7)',
                          ],
                        },
                      ],
                    }}
                  />
                </div>

                {/* Leave Status */}
                <div className="bg-white rounded-lg shadow p-6">
                  <h3 className="text-lg font-medium text-gray-900 mb-4">Leave Status</h3>
                  <AnalyticChart
                    type="pie"
                    data={{
                      labels: analytics.leaveStatus.labels,
                      datasets: [
                        {
                          data: analytics.leaveStatus.counts,
                          backgroundColor: [
                            'rgba(245, 158, 11, 0.7)',
                            'rgba(16, 185, 129, 0.7)',
                            'rgba(239, 68, 68, 0.7)',
                          ],
                        },
                      ],
                    }}
                  />
                </div>

                {/* Role Distribution */}
                <div className="bg-white rounded-lg shadow p-6">
                  <h3 className="text-lg font-medium text-gray-900 mb-4">Role Distribution</h3>
                  <AnalyticChart
                    type="polarArea"
                    data={{
                      labels: analytics.roleDistribution.labels,
                      datasets: [
                        {
                          data: analytics.roleDistribution.counts,
                          backgroundColor: [
                            'rgba(59, 130, 246, 0.7)',
                            'rgba(16, 185, 129, 0.7)',
                            'rgba(124, 58, 237, 0.7)',
                            'rgba(245, 158, 11, 0.7)',
                            'rgba(239, 68, 68, 0.7)',
                          ],
                        },
                      ],
                    }}
                  />
                </div>

                {/* Contract Type Distribution */}
                <div className="bg-white rounded-lg shadow p-6">
                  <h3 className="text-lg font-medium text-gray-900 mb-4">Contract Type Distribution</h3>
                  <AnalyticChart
                    type="bar"
                    data={{
                      labels: analytics.contractTypeDistribution.labels,
                      datasets: [
                        {
                          label: 'Employees',
                          data: analytics.contractTypeDistribution.counts,
                          backgroundColor: 'rgba(124, 58, 237, 0.5)',
                        },
                      ],
                    }}
                  />
                </div>
              </div>

              {/* Employee Timeline */}
              <div className="bg-white rounded-lg shadow p-6 mb-6">
                <h3 className="text-lg font-medium text-gray-900 mb-4">Employee Trend (6 Months)</h3>
                <AnalyticChart
                  type="line"
                  data={{
                    labels: analytics.employeeTimeline.months,
                    datasets: [
                      {
                        label: 'Active',
                        data: analytics.employeeTimeline.active,
                        borderColor: 'rgb(16, 185, 129)',
                        backgroundColor: 'rgba(16, 185, 129, 0.1)',
                        tension: 0.3,
                      },
                      {
                        label: 'Inactive',
                        data: analytics.employeeTimeline.inactive,
                        borderColor: 'rgb(239, 68, 68)',
                        backgroundColor: 'rgba(239, 68, 68, 0.1)',
                        tension: 0.3,
                      },
                    ],
                  }}
                />
              </div>

              {/* Featured Image */}
              <div className="bg-white rounded-lg shadow p-4 mb-6">
                <img
                  src="https://pixabay.com/get/g9e4f7e4aa2f319a4d947cfb1fcbab9b990285aa611e8a055c5d316b769eb9ec7cb97f6ad0af64559a374b5b6a9c2e13e786bc54cbe2bf994cacbb443b35dd916_1280.jpg"
                  alt="Office workplace"
                  className="w-full h-auto rounded-lg object-cover"
                />
              </div>
            </div>
          ) : (
            <div className="mt-6 bg-white shadow overflow-hidden sm:rounded-md">
              <div className="px-4 py-5 sm:p-6 text-center">
                <p className="text-gray-500">No analytics data available.</p>
              </div>
            </div>
          )}
        </div>
      </div>
    </div>
  );
};

export default AnalyticsPage;
