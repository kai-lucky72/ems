import { useState, useEffect } from 'react';
import { NextPage } from 'next';
import Layout from '@/components/Layout';
import MessageForm from '@/components/MessageForm';
import { fetchEmployees, fetchMessages, sendMessage } from '@/lib/api';
import { Message, MessageFormData } from '@/types';

interface Employee {
  id: number;
  name: string;
  email: string;
  departmentName: string;
  isActive: boolean;
}

const MessagingPage: NextPage = () => {
  const [messages, setMessages] = useState<Message[]>([]);
  const [employees, setEmployees] = useState<Employee[]>([]);
  const [isFormOpen, setIsFormOpen] = useState(false);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [searchQuery, setSearchQuery] = useState('');
  const [selectedEmployeeId, setSelectedEmployeeId] = useState<number | null>(null);

  useEffect(() => {
    const loadData = async () => {
      try {
        setLoading(true);
        // For development without backend
        const mockMessages: Message[] = [];
        const mockEmployees: Employee[] = [
          {
            id: 1,
            name: 'John Doe',
            email: 'john@example.com',
            departmentName: 'Engineering',
            isActive: true
          },
          {
            id: 2,
            name: 'Jane Smith',
            email: 'jane@example.com',
            departmentName: 'Marketing',
            isActive: true
          }
        ];
        
        setMessages(mockMessages);
        setEmployees(mockEmployees.filter(emp => emp.isActive));
        setError('');
      } catch (err: any) {
        console.error('Failed to fetch data:', err);
        setError('Failed to load messaging data. Please try again later.');
      } finally {
        setLoading(false);
      }
    };

    loadData();
  }, []);

  const handleNewMessage = (employeeId: number | null = null) => {
    setSelectedEmployeeId(employeeId);
    setIsFormOpen(true);
  };

  const handleCloseForm = () => {
    setIsFormOpen(false);
    setSelectedEmployeeId(null);
  };

  const handleSubmitMessage = async (formData: MessageFormData) => {
    try {
      const newMessage = await sendMessage(formData);
      setMessages(prev => [newMessage, ...prev]);
      setIsFormOpen(false);
      setSelectedEmployeeId(null);
    } catch (err: any) {
      throw new Error(err.response?.data?.message || 'Failed to send message');
    }
  };

  // Filter messages based on search query
  const filteredMessages = messages.filter(message => {
    return (
      message.employeeName.toLowerCase().includes(searchQuery.toLowerCase()) ||
      message.subject.toLowerCase().includes(searchQuery.toLowerCase()) ||
      message.content.toLowerCase().includes(searchQuery.toLowerCase())
    );
  });

  // Format date to a readable format
  const formatDate = (dateString: string) => {
    const date = new Date(dateString);
    return date.toLocaleDateString('en-US', {
      year: 'numeric',
      month: 'short',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
  };

  // Get status badge class
  const getStatusBadgeClass = (status: string) => {
    switch (status) {
      case 'SENT':
        return 'bg-green-100 text-green-800';
      case 'FAILED':
        return 'bg-red-100 text-red-800';
      default:
        return 'bg-gray-100 text-gray-800';
    }
  };

  return (
    <Layout>
      <div className="space-y-6">
        <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between">
          <h1 className="text-2xl font-bold">Employee Messaging</h1>
          <button
            onClick={() => handleNewMessage()}
            className="mt-3 sm:mt-0 btn btn-primary"
          >
            New Message
          </button>
        </div>

        {error && (
          <div className="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded">
            {error}
          </div>
        )}

        <div className="bg-white rounded-lg shadow overflow-hidden">
          <div className="p-4 border-b">
            <div className="flex">
              <div className="flex-1">
                <input
                  type="text"
                  placeholder="Search messages..."
                  value={searchQuery}
                  onChange={(e) => setSearchQuery(e.target.value)}
                  className="input"
                />
              </div>
            </div>
          </div>

          {loading ? (
            <div className="flex justify-center py-12">
              <div className="animate-spin rounded-full h-12 w-12 border-t-2 border-b-2 border-primary"></div>
            </div>
          ) : filteredMessages.length === 0 ? (
            <div className="p-12 text-center">
              <div className="text-gray-500 mb-4">No messages found</div>
              <p className="text-gray-400 text-sm mb-6">
                Start communicating with your employees by sending a message
              </p>
              <button
                onClick={() => handleNewMessage()}
                className="btn btn-secondary"
              >
                Send Your First Message
              </button>
            </div>
          ) : (
            <div className="divide-y divide-gray-200">
              {filteredMessages.map((message) => (
                <div key={message.id} className="p-4 hover:bg-gray-50">
                  <div className="flex justify-between items-start">
                    <div className="space-y-1">
                      <div className="flex items-center">
                        <h3 className="text-lg font-medium text-gray-900">{message.subject}</h3>
                        <span 
                          className={`ml-3 px-2 inline-flex text-xs leading-5 font-semibold rounded-full ${getStatusBadgeClass(message.status)}`}
                        >
                          {message.status}
                        </span>
                      </div>
                      <p className="text-sm text-gray-500">
                        To: <span className="font-medium">{message.employeeName}</span>
                      </p>
                      <p className="text-sm text-gray-500">
                        Sent: {formatDate(message.sentAt)}
                      </p>
                    </div>
                    <button 
                      onClick={() => handleNewMessage(message.employeeId)}
                      className="text-blue-600 hover:text-blue-800 text-sm font-medium"
                    >
                      Reply
                    </button>
                  </div>
                  <div className="mt-3 text-sm text-gray-600">
                    <p className="line-clamp-2">{message.content}</p>
                  </div>
                </div>
              ))}
            </div>
          )}
        </div>

        {/* Employee Directory for Quick Messaging */}
        <div className="bg-white rounded-lg shadow">
          <div className="px-6 py-4 border-b">
            <h2 className="text-lg font-medium">Employee Directory</h2>
          </div>
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4 p-4">
            {employees.map((employee) => (
              <div key={employee.id} className="border rounded p-4 flex justify-between items-center">
                <div>
                  <h3 className="font-medium">{employee.name}</h3>
                  <p className="text-sm text-gray-500">{employee.departmentName}</p>
                  <p className="text-xs text-gray-500">{employee.email}</p>
                </div>
                <button
                  onClick={() => handleNewMessage(employee.id)}
                  className="text-blue-600 hover:text-blue-800"
                >
                  Message
                </button>
              </div>
            ))}
          </div>
        </div>
      </div>

      {/* Message Form Modal */}
      {isFormOpen && (
        <div className="fixed inset-0 overflow-y-auto z-50 flex items-center justify-center bg-black bg-opacity-50">
          <div className="bg-white rounded-lg shadow-xl max-w-2xl w-full mx-4 p-6">
            <div className="flex justify-between items-center mb-6">
              <h2 className="text-xl font-bold">
                Send Message
              </h2>
              <button
                onClick={handleCloseForm}
                className="text-gray-400 hover:text-gray-500"
              >
                <svg className="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M6 18L18 6M6 6l12 12" />
                </svg>
              </button>
            </div>
            <MessageForm
              employees={employees}
              initialEmployeeId={selectedEmployeeId}
              onSubmit={handleSubmitMessage}
              onCancel={handleCloseForm}
            />
          </div>
        </div>
      )}
    </Layout>
  );
};

export default MessagingPage;