import { useState, useEffect } from 'react';
import { NextPage } from 'next';
import { useRouter } from 'next/router';
import Layout from '@/components/Layout';
import ProtectedRoute from '@/components/auth/ProtectedRoute';
import MessageForm from '@/components/MessageForm';
import { fetchEmployees, sendMessage } from '@/lib/api';
import { MessageFormData } from '@/types';

interface Employee {
  id: number;
  name: string;
  email: string;
  departmentName: string;
  isActive: boolean;
}

const MessagingPage: NextPage = () => {
  const router = useRouter();
  const { employeeId: queryEmployeeId } = router.query;
  
  const [employees, setEmployees] = useState<Employee[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const [initialEmployeeId, setInitialEmployeeId] = useState<number | null>(null);

  useEffect(() => {
    const loadEmployees = async () => {
      try {
        setLoading(true);
        const employeesData = await fetchEmployees();
        
        // Only show active employees
        const activeEmployees = employeesData.filter((emp: Employee) => emp.isActive);
        setEmployees(activeEmployees);
        
        // Check if there's an employeeId in the URL query
        if (queryEmployeeId && !Array.isArray(queryEmployeeId)) {
          const id = parseInt(queryEmployeeId);
          if (!isNaN(id) && activeEmployees.some((emp: Employee) => emp.id === id)) {
            setInitialEmployeeId(id);
          }
        }
        
        setError('');
      } catch (err: any) {
        console.error('Failed to fetch employees:', err);
        setError('Failed to load employees. Please try again later.');
      } finally {
        setLoading(false);
      }
    };

    loadEmployees();
  }, [queryEmployeeId]);

  const handleSubmitMessage = async (formData: MessageFormData) => {
    try {
      setLoading(true);
      await sendMessage(formData);
      setSuccess(`Message sent successfully to ${employees.find((emp: Employee) => emp.id === formData.employeeId)?.name}`);
      
      // Clear success message after 5 seconds
      setTimeout(() => {
        setSuccess('');
      }, 5000);
      
      return Promise.resolve();
    } catch (err: any) {
      console.error('Failed to send message:', err);
      throw new Error(err.response?.data?.message || 'Failed to send message');
    } finally {
      setLoading(false);
    }
  };

  return (
    <ProtectedRoute requiredRole="MANAGER">
      <Layout>
        <div className="space-y-6">
          <div className="flex items-center justify-between">
            <h1 className="text-2xl font-bold">Send Message</h1>
          </div>

          {error && (
            <div className="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded">
              {error}
            </div>
          )}

          {success && (
            <div className="bg-green-50 border border-green-200 text-green-700 px-4 py-3 rounded">
              {success}
            </div>
          )}

          {loading && employees.length === 0 ? (
            <div className="flex justify-center py-12">
              <div className="animate-spin rounded-full h-12 w-12 border-t-2 border-b-2 border-primary"></div>
            </div>
          ) : employees.length === 0 ? (
            <div className="bg-white rounded-lg shadow p-6 text-center">
              <p className="text-gray-500">No employees available to message.</p>
            </div>
          ) : (
            <div className="bg-white rounded-lg shadow p-6">
              <MessageForm
                employees={employees}
                initialEmployeeId={initialEmployeeId}
                onSubmit={handleSubmitMessage}
                onCancel={() => router.push('/dashboard/messages')}
              />
            </div>
          )}
        </div>
      </Layout>
    </ProtectedRoute>
  );
};

export default MessagingPage;