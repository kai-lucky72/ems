import { useState, useEffect } from 'react';
import Head from 'next/head';
import { fetchEmployees, fetchMessages, sendMessage } from '../../lib/api';
import MessageForm from '../../components/MessageForm';

interface Employee {
  id: number;
  name: string;
  email: string;
  departmentName: string;
  isActive: boolean;
}

interface Message {
  id: number;
  employeeId: number;
  employeeName: string;
  subject: string;
  content: string;
  sentAt: string;
  status: 'SENT' | 'FAILED';
}

const MessagingPage = () => {
  const [messages, setMessages] = useState<Message[]>([]);
  const [employees, setEmployees] = useState<Employee[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [isFormOpen, setIsFormOpen] = useState(false);
  const [selectedEmployeeId, setSelectedEmployeeId] = useState<number | null>(null);

  const loadData = async () => {
    try {
      setLoading(true);
      const [employeesData, messagesData] = await Promise.all([
        fetchEmployees(),
        fetchMessages()
      ]);
      setEmployees(employeesData);
      setMessages(messagesData);
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

  const handleOpenForm = (employeeId: number | null = null) => {
    setSelectedEmployeeId(employeeId);
    setIsFormOpen(true);
  };

  const handleCloseForm = () => {
    setIsFormOpen(false);
    setSelectedEmployeeId(null);
  };

  const handleSubmit = async (formData: { 
    employeeId: number;
    subject: string;
    content: string;
  }) => {
    try {
      await sendMessage(formData);
      handleCloseForm();
      await loadData();
    } catch (err: any) {
      setError(err.message || 'Failed to send message');
    }
  };

  const formatDate = (dateString: string) => {
    return new Date(dateString).toLocaleString();
  };

  // Group messages by employee
  const messagesGroupedByEmployee = messages.reduce<Record<number, Message[]>>((acc, message) => {
    if (!acc[message.employeeId]) {
      acc[message.employeeId] = [];
    }
    acc[message.employeeId].push(message);
    return acc;
  }, {});

  return (
    <div className="min-h-screen bg-gray-50">
      <Head>
        <title>Messaging | Employee Management System</title>
      </Head>

      <div className="py-6">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 md:px-8 flex justify-between items-center">
          <h1 className="text-2xl font-semibold text-gray-900">Messaging</h1>
          <button
            onClick={() => handleOpenForm()}
            className="inline-flex items-center px-4 py-2 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-blue-600 hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500"
          >
            New Message
          </button>
        </div>

        <div className="max-w-7xl mx-auto px-4 sm:px-6 md:px-8 mt-6">
          {error && (
            <div className="mb-4 p-4 bg-red-100 border-l-4 border-red-500 text-red-700">
              {error}
            </div>
          )}

          {loading ? (
            <div className="flex justify-center items-center h-64">
              <div className="h-8 w-8 animate-spin rounded-full border-b-2 border-t-2 border-blue-500"></div>
            </div>
          ) : Object.keys(messagesGroupedByEmployee).length === 0 ? (
            <div className="bg-white shadow overflow-hidden sm:rounded-md">
              <div className="px-4 py-5 sm:p-6 text-center">
                <p className="text-gray-500">No messages found. Send one to get started.</p>
              </div>
            </div>
          ) : (
            <div className="space-y-6">
              {Object.entries(messagesGroupedByEmployee).map(([employeeId, employeeMessages]) => {
                const employee = employees.find(e => e.id === Number(employeeId));
                if (!employee) return null;
                
                return (
                  <div key={employeeId} className="bg-white shadow overflow-hidden sm:rounded-lg">
                    <div className="px-4 py-5 sm:px-6 flex justify-between items-center">
                      <div>
                        <h3 className="text-lg leading-6 font-medium text-gray-900">
                          {employee.name}
                        </h3>
                        <p className="mt-1 max-w-2xl text-sm text-gray-500">
                          {employee.email} · {employee.departmentName}
                        </p>
                      </div>
                      <button
                        onClick={() => handleOpenForm(Number(employeeId))}
                        className="inline-flex items-center px-3 py-1 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-indigo-600 hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500"
                      >
                        Send Message
                      </button>
                    </div>
                    <div className="border-t border-gray-200">
                      <ul className="divide-y divide-gray-200">
                        {employeeMessages.sort((a, b) => new Date(b.sentAt).getTime() - new Date(a.sentAt).getTime()).map((message) => (
                          <li key={message.id} className="px-4 py-4">
                            <div className="flex justify-between items-start">
                              <div className="flex-1">
                                <div className="flex items-center">
                                  <h4 className="text-base font-semibold text-gray-900 mr-2">
                                    {message.subject}
                                  </h4>
                                  <span className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium ${
                                    message.status === 'SENT' 
                                      ? 'bg-green-100 text-green-800' 
                                      : 'bg-red-100 text-red-800'
                                  }`}>
                                    {message.status}
                                  </span>
                                </div>
                                <p className="mt-1 text-sm text-gray-600 whitespace-pre-line">
                                  {message.content}
                                </p>
                                <p className="mt-2 text-xs text-gray-500">
                                  {formatDate(message.sentAt)}
                                </p>
                              </div>
                            </div>
                          </li>
                        ))}
                      </ul>
                    </div>
                  </div>
                );
              })}
            </div>
          )}
        </div>
      </div>

      {isFormOpen && (
        <MessageForm
          employees={employees.filter(emp => emp.isActive)}
          initialEmployeeId={selectedEmployeeId}
          onSubmit={handleSubmit}
          onCancel={handleCloseForm}
        />
      )}
    </div>
  );
};

export default MessagingPage;
