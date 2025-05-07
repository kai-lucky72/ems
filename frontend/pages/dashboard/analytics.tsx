import { useState, useEffect } from 'react';
import { NextPage } from 'next';
import Layout from '@/components/Layout';
import ProtectedRoute from '@/components/auth/ProtectedRoute';
import AnalyticChart from '@/components/AnalyticChart';
import { fetchAnalytics } from '@/lib/api';

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

const AnalyticsPage: NextPage = () => {
  const [analyticsData, setAnalyticsData] = useState<AnalyticsData | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    const loadAnalytics = async () => {
      try {
        setLoading(true);
        const data = await fetchAnalytics();
        setAnalyticsData(data);
        setError('');
      } catch (err: any) {
        console.error('Failed to fetch analytics:', err);
        setError('Failed to load analytics data. Please try again later.');
      } finally {
        setLoading(false);
      }
    };

    loadAnalytics();
  }, []);

  return (
    <ProtectedRoute requiredRole="MANAGER">
      <Layout>
        <div className="space-y-6">
          <h1 className="text-2xl font-bold">Analytics Dashboard</h1>

          {error ? (
            <div className="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded">
              {error}
            </div>
          ) : loading ? (
            <div className="flex justify-center py-12">
              <div className="animate-spin rounded-full h-12 w-12 border-t-2 border-b-2 border-primary"></div>
            </div>
          ) : !analyticsData ? (
            <div className="bg-white rounded-lg shadow p-6 text-center">
              <p className="text-gray-500">No analytics data available.</p>
            </div>
          ) : (
            <>
              {/* Overview Numbers */}
              <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
                <div className="bg-white rounded-lg shadow p-4">
                  <h3 className="text-sm font-medium text-gray-500">Total Salary Budget</h3>
                  <p className="text-2xl font-semibold mt-2">
                    ${analyticsData.salaryData.totalGross.toLocaleString()}
                  </p>
                  <p className="text-sm text-gray-500 mt-1">
                    ${analyticsData.salaryData.totalNet.toLocaleString()} after deductions
                  </p>
                </div>
                <div className="bg-white rounded-lg shadow p-4">
                  <h3 className="text-sm font-medium text-gray-500">Average Salary</h3>
                  <p className="text-2xl font-semibold mt-2">
                    ${analyticsData.salaryData.averageSalary.toLocaleString()}
                  </p>
                  <p className="text-sm text-gray-500 mt-1">Per employee</p>
                </div>
                <div className="bg-white rounded-lg shadow p-4">
                  <h3 className="text-sm font-medium text-gray-500">Total Employees</h3>
                  <p className="text-2xl font-semibold mt-2">
                    {analyticsData.employeeDistribution.counts.reduce((sum, c) => sum + c, 0)}
                  </p>
                  <p className="text-sm text-gray-500 mt-1">
                    Across {analyticsData.employeeDistribution.labels.length} departments
                  </p>
                </div>
                <div className="bg-white rounded-lg shadow p-4">
                  <h3 className="text-sm font-medium text-gray-500">Pending Leaves</h3>
                  <p className="text-2xl font-semibold mt-2">
                    {analyticsData.leaveStatus.labels.includes('PENDING')
                      ? analyticsData.leaveStatus.counts[analyticsData.leaveStatus.labels.indexOf('PENDING')]
                      : 0}
                  </p>
                  <p className="text-sm text-gray-500 mt-1">Awaiting approval</p>
                </div>
              </div>

              {/* Budget Analysis */}
              <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
                <div className="bg-white rounded-lg shadow p-6">
                  <h2 className="text-lg font-semibold mb-4">Department Budget vs Actual</h2>
                  <div className="h-80">
                    <AnalyticChart
                      type="bar"
                      data={{
                        labels: analyticsData.departmentBudget.labels,
                        datasets: [
                          {
                            label: 'Budget',
                            data: analyticsData.departmentBudget.budget,
                            backgroundColor: 'rgba(66, 153, 225, 0.5)',
                            borderColor: 'rgb(66, 153, 225)',
                          },
                          {
                            label: 'Actual',
                            data: analyticsData.departmentBudget.actual,
                            backgroundColor: 'rgba(72, 187, 120, 0.5)',
                            borderColor: 'rgb(72, 187, 120)',
                          },
                        ],
                      }}
                    />
                  </div>
                </div>

                <div className="bg-white rounded-lg shadow p-6">
                  <h2 className="text-lg font-semibold mb-4">Department Salary Distribution</h2>
                  <div className="h-80">
                    <AnalyticChart
                      type="pie"
                      data={{
                        labels: analyticsData.salaryData.departmentSalaries.map(d => d.department),
                        datasets: [
                          {
                            data: analyticsData.salaryData.departmentSalaries.map(d => d.totalSalary),
                            backgroundColor: [
                              'rgba(66, 153, 225, 0.7)',
                              'rgba(72, 187, 120, 0.7)',
                              'rgba(237, 100, 166, 0.7)',
                              'rgba(246, 173, 85, 0.7)',
                              'rgba(159, 122, 234, 0.7)',
                              'rgba(226, 232, 240, 0.7)',
                              'rgba(113, 128, 150, 0.7)',
                              'rgba(245, 101, 101, 0.7)',
                            ],
                            borderColor: [
                              'rgb(66, 153, 225)',
                              'rgb(72, 187, 120)',
                              'rgb(237, 100, 166)',
                              'rgb(246, 173, 85)',
                              'rgb(159, 122, 234)',
                              'rgb(226, 232, 240)',
                              'rgb(113, 128, 150)',
                              'rgb(245, 101, 101)',
                            ],
                          },
                        ],
                      }}
                    />
                  </div>
                </div>
              </div>

              {/* Employee Distributions */}
              <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
                <div className="bg-white rounded-lg shadow p-6">
                  <h2 className="text-lg font-semibold mb-4">Employee Distribution by Department</h2>
                  <div className="h-80">
                    <AnalyticChart
                      type="bar"
                      data={{
                        labels: analyticsData.employeeDistribution.labels,
                        datasets: [
                          {
                            data: analyticsData.employeeDistribution.counts,
                            backgroundColor: 'rgba(66, 153, 225, 0.7)',
                            borderColor: 'rgb(66, 153, 225)',
                          },
                        ],
                      }}
                    />
                  </div>
                </div>

                <div className="bg-white rounded-lg shadow p-6">
                  <h2 className="text-lg font-semibold mb-4">Contract Type Distribution</h2>
                  <div className="h-80">
                    <AnalyticChart
                      type="doughnut"
                      data={{
                        labels: analyticsData.contractTypeDistribution.labels.map(label => 
                          label === 'FULL_TIME' ? 'Full Time' : 
                          label === 'PART_TIME' ? 'Part Time' : 'Remote'
                        ),
                        datasets: [
                          {
                            data: analyticsData.contractTypeDistribution.counts,
                            backgroundColor: [
                              'rgba(66, 153, 225, 0.7)',
                              'rgba(72, 187, 120, 0.7)',
                              'rgba(237, 100, 166, 0.7)',
                            ],
                            borderColor: [
                              'rgb(66, 153, 225)',
                              'rgb(72, 187, 120)',
                              'rgb(237, 100, 166)',
                            ],
                          },
                        ],
                      }}
                    />
                  </div>
                </div>
              </div>

              {/* Role Distribution and Leave Status */}
              <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
                <div className="bg-white rounded-lg shadow p-6">
                  <h2 className="text-lg font-semibold mb-4">Job Role Distribution</h2>
                  <div className="h-80">
                    <AnalyticChart
                      type="polarArea"
                      data={{
                        labels: analyticsData.roleDistribution.labels,
                        datasets: [
                          {
                            data: analyticsData.roleDistribution.counts,
                            backgroundColor: [
                              'rgba(66, 153, 225, 0.7)',
                              'rgba(72, 187, 120, 0.7)',
                              'rgba(237, 100, 166, 0.7)',
                              'rgba(246, 173, 85, 0.7)',
                              'rgba(159, 122, 234, 0.7)',
                              'rgba(226, 232, 240, 0.7)',
                              'rgba(113, 128, 150, 0.7)',
                              'rgba(245, 101, 101, 0.7)',
                            ],
                          },
                        ],
                      }}
                    />
                  </div>
                </div>

                <div className="bg-white rounded-lg shadow p-6">
                  <h2 className="text-lg font-semibold mb-4">Leave Status Distribution</h2>
                  <div className="h-80">
                    <AnalyticChart
                      type="bar"
                      data={{
                        labels: analyticsData.leaveStatus.labels.map(label => 
                          label.charAt(0) + label.slice(1).toLowerCase()
                        ),
                        datasets: [
                          {
                            data: analyticsData.leaveStatus.counts,
                            backgroundColor: [
                              'rgba(246, 173, 85, 0.7)',  // Pending
                              'rgba(72, 187, 120, 0.7)',  // Approved
                              'rgba(245, 101, 101, 0.7)', // Denied
                            ],
                            borderColor: [
                              'rgb(246, 173, 85)',
                              'rgb(72, 187, 120)',
                              'rgb(245, 101, 101)',
                            ],
                          },
                        ],
                      }}
                    />
                  </div>
                </div>
              </div>

              {/* Employee Timeline */}
              <div className="bg-white rounded-lg shadow p-6">
                <h2 className="text-lg font-semibold mb-4">Employee Activity Timeline (Last 12 Months)</h2>
                <div className="h-80">
                  <AnalyticChart
                    type="line"
                    data={{
                      labels: analyticsData.employeeTimeline.months,
                      datasets: [
                        {
                          label: 'Active Employees',
                          data: analyticsData.employeeTimeline.active,
                          borderColor: 'rgb(72, 187, 120)',
                          backgroundColor: 'rgba(72, 187, 120, 0.1)',
                          tension: 0.3,
                        },
                        {
                          label: 'Inactive Employees',
                          data: analyticsData.employeeTimeline.inactive,
                          borderColor: 'rgb(245, 101, 101)',
                          backgroundColor: 'rgba(245, 101, 101, 0.1)',
                          tension: 0.3,
                        },
                      ],
                    }}
                  />
                </div>
              </div>
            </>
          )}
        </div>
      </Layout>
    </ProtectedRoute>
  );
};

export default AnalyticsPage;