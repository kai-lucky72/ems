import { useState, useEffect } from 'react';
import { NextPage } from 'next';
import Layout from '@/components/Layout';
import ProtectedRoute from '@/components/auth/ProtectedRoute';
import { getUser } from '@/lib/auth';
import { Salary, Deduction } from '@/types';

const MySalaryPage: NextPage = () => {
  const [salary, setSalary] = useState<Salary | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [salaryHistory, setSalaryHistory] = useState<{ month: string; amount: number }[]>([]);

  useEffect(() => {
    // Simulate API call
    setTimeout(() => {
      try {
        // Mock salary data
        const mockSalary: Salary = {
          id: 1,
          employeeId: 1,
          employeeName: 'John Doe',
          departmentName: 'Engineering',
          grossSalary: 5000,
          netSalary: 3850,
          deductions: [
            {
              id: 1,
              type: 'TAX',
              name: 'Income Tax',
              value: 20,
              isPercentage: true
            },
            {
              id: 2,
              type: 'INSURANCE',
              name: 'Health Insurance',
              value: 150,
              isPercentage: false
            }
          ]
        };
        
        // Mock salary history
        const mockSalaryHistory = [
          { month: 'January 2023', amount: 3850 },
          { month: 'February 2023', amount: 3850 },
          { month: 'March 2023', amount: 3850 },
          { month: 'April 2023', amount: 3850 },
          { month: 'May 2023', amount: 3950 },
          { month: 'June 2023', amount: 3950 }
        ];
        
        setSalary(mockSalary);
        setSalaryHistory(mockSalaryHistory);
        setLoading(false);
      } catch (err) {
        console.error('Error fetching salary data:', err);
        setError('Failed to load salary data. Please try again later.');
        setLoading(false);
      }
    }, 1000);
  }, []);

  // Calculate deduction amount
  const calculateDeductionAmount = (deduction: Deduction, grossSalary: number) => {
    if (deduction.isPercentage) {
      return (grossSalary * deduction.value) / 100;
    }
    return deduction.value;
  };

  return (
    <ProtectedRoute>
      <Layout>
        <div className="space-y-6">
          <div className="flex items-center justify-between">
            <h1 className="text-2xl font-bold">My Salary</h1>
          </div>
          
          {error ? (
            <div className="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded">
              {error}
            </div>
          ) : loading ? (
            <div className="flex justify-center py-12">
              <div className="animate-spin rounded-full h-12 w-12 border-t-2 border-b-2 border-primary"></div>
            </div>
          ) : !salary ? (
            <div className="bg-white rounded-lg shadow p-6 text-center">
              <p className="text-gray-500">No salary information available.</p>
            </div>
          ) : (
            <>
              {/* Current Salary Overview */}
              <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
                <div className="bg-white rounded-lg shadow p-6">
                  <h2 className="text-lg font-semibold mb-4">Gross Salary</h2>
                  <div className="flex items-center">
                    <div className="p-3 rounded-full bg-blue-100 text-blue-600 mr-4">
                      <svg className="w-6 h-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 8c-1.657 0-3 .895-3 2s1.343 2 3 2 3 .895 3 2-1.343 2-3 2m0-8c1.11 0 2.08.402 2.599 1M12 8V7m0 1v8m0 0v1m0-1c-1.11 0-2.08-.402-2.599-1M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
                      </svg>
                    </div>
                    <div>
                      <p className="text-sm text-gray-500">Monthly</p>
                      <p className="text-xl font-bold">${salary.grossSalary.toLocaleString()}</p>
                      <p className="text-sm text-gray-500">Annual: ${(salary.grossSalary * 12).toLocaleString()}</p>
                    </div>
                  </div>
                </div>
                
                <div className="bg-white rounded-lg shadow p-6">
                  <h2 className="text-lg font-semibold mb-4">Net Salary</h2>
                  <div className="flex items-center">
                    <div className="p-3 rounded-full bg-green-100 text-green-600 mr-4">
                      <svg className="w-6 h-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M17 9V7a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2m2 4h10a2 2 0 002-2v-6a2 2 0 00-2-2H9a2 2 0 00-2 2v6a2 2 0 002 2zm7-5a2 2 0 11-4 0 2 2 0 014 0z" />
                      </svg>
                    </div>
                    <div>
                      <p className="text-sm text-gray-500">Take Home</p>
                      <p className="text-xl font-bold">${salary.netSalary.toLocaleString()}</p>
                      <p className="text-sm text-gray-500">Annual: ${(salary.netSalary * 12).toLocaleString()}</p>
                    </div>
                  </div>
                </div>
                
                <div className="bg-white rounded-lg shadow p-6">
                  <h2 className="text-lg font-semibold mb-4">Deductions</h2>
                  <div className="flex items-center">
                    <div className="p-3 rounded-full bg-red-100 text-red-600 mr-4">
                      <svg className="w-6 h-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M15 12H9m12 0a9 9 0 11-18 0 9 9 0 0118 0z" />
                      </svg>
                    </div>
                    <div>
                      <p className="text-sm text-gray-500">Total</p>
                      <p className="text-xl font-bold">
                        ${(salary.grossSalary - salary.netSalary).toLocaleString()}
                      </p>
                      <p className="text-sm text-gray-500">
                        {(((salary.grossSalary - salary.netSalary) / salary.grossSalary) * 100).toFixed(1)}% of gross
                      </p>
                    </div>
                  </div>
                </div>
              </div>
              
              {/* Detailed Breakdown */}
              <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                <div className="bg-white rounded-lg shadow overflow-hidden">
                  <div className="px-6 py-5 border-b border-gray-200">
                    <h2 className="text-lg font-semibold">Salary Breakdown</h2>
                  </div>
                  <div className="p-6">
                    <div className="flex justify-between py-3 border-b border-gray-100">
                      <span className="font-medium">Gross Salary</span>
                      <span className="font-semibold">${salary.grossSalary.toLocaleString()}</span>
                    </div>
                    
                    {/* Deductions */}
                    {salary.deductions.map((deduction) => (
                      <div key={deduction.id} className="flex justify-between py-3 border-b border-gray-100">
                        <span className="text-gray-600">
                          {deduction.name}
                          {deduction.isPercentage && ` (${deduction.value}%)`}
                        </span>
                        <span className="text-red-600">
                          -${calculateDeductionAmount(deduction, salary.grossSalary).toLocaleString()}
                        </span>
                      </div>
                    ))}
                    
                    {/* Net Salary */}
                    <div className="flex justify-between py-3 mt-3 border-t border-gray-200">
                      <span className="font-medium">Net Salary</span>
                      <span className="font-semibold text-green-600">${salary.netSalary.toLocaleString()}</span>
                    </div>
                  </div>
                </div>
                
                {/* Salary History */}
                <div className="bg-white rounded-lg shadow overflow-hidden">
                  <div className="px-6 py-5 border-b border-gray-200">
                    <h2 className="text-lg font-semibold">Salary History</h2>
                  </div>
                  <div className="p-6">
                    <div className="space-y-4">
                      {salaryHistory.map((item, index) => (
                        <div key={index} className="flex justify-between py-2 border-b border-gray-100">
                          <span>{item.month}</span>
                          <span className="font-medium">${item.amount.toLocaleString()}</span>
                        </div>
                      ))}
                    </div>
                  </div>
                </div>
              </div>
              
              {/* Pay slip Download */}
              <div className="bg-white rounded-lg shadow p-6">
                <h2 className="text-lg font-semibold mb-4">Download Pay Slips</h2>
                <p className="text-gray-600 mb-4">
                  Pay slips are available for download for the last 6 months. For older pay slips, please contact HR.
                </p>
                <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
                  {['June 2023', 'May 2023', 'April 2023', 'March 2023', 'February 2023', 'January 2023'].map((month, index) => (
                    <button
                      key={index}
                      className="flex items-center justify-center px-4 py-2 border border-gray-300 rounded-md text-sm font-medium text-gray-700 bg-white hover:bg-gray-50"
                    >
                      <svg className="w-4 h-4 mr-2" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 10v6m0 0l-3-3m3 3l3-3m2 8H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
                      </svg>
                      {month}
                    </button>
                  ))}
                </div>
              </div>
            </>
          )}
        </div>
      </Layout>
    </ProtectedRoute>
  );
};

export default MySalaryPage;