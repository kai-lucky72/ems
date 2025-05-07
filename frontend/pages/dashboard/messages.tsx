import { useState, useEffect } from 'react';
import { NextPage } from 'next';
import Link from 'next/link';
import Layout from '@/components/Layout';
import ProtectedRoute from '@/components/auth/ProtectedRoute';
import { fetchMessages } from '@/lib/api';
import { isManager } from '@/lib/auth';
import { Message } from '@/types';

const MessagesPage: NextPage = () => {
  const [messages, setMessages] = useState<Message[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [searchQuery, setSearchQuery] = useState('');
  const userIsManager = isManager();

  useEffect(() => {
    const loadMessages = async () => {
      try {
        setLoading(true);
        const data = await fetchMessages();
        setMessages(data);
        setError('');
      } catch (err: any) {
        console.error('Failed to fetch messages:', err);
        setError('Failed to load messages. Please try again later.');
      } finally {
        setLoading(false);
      }
    };

    loadMessages();
  }, []);

  // Format date to a more readable format
  const formatDate = (dateString: string) => {
    return new Date(dateString).toLocaleString('en-US', {
      year: 'numeric',
      month: 'short',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit',
    });
  };

  // Filter messages based on search query
  const filteredMessages = messages.filter(message => {
    const searchLower = searchQuery.toLowerCase();
    return (
      message.subject.toLowerCase().includes(searchLower) ||
      message.employeeName.toLowerCase().includes(searchLower) ||
      message.content.toLowerCase().includes(searchLower)
    );
  });

  return (
    <ProtectedRoute>
      <Layout>
        <div className="space-y-6">
          <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between">
            <h1 className="text-2xl font-bold">Messages</h1>
            {userIsManager && (
              <Link 
                href="/dashboard/messaging" 
                className="mt-3 sm:mt-0 inline-flex items-center px-4 py-2 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-primary hover:bg-primary-dark focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-primary"
              >
                <svg
                  className="-ml-1 mr-2 h-5 w-5"
                  xmlns="http://www.w3.org/2000/svg"
                  viewBox="0 0 20 20"
                  fill="currentColor"
                  aria-hidden="true"
                >
                  <path
                    fillRule="evenodd"
                    d="M10 3a1 1 0 011 1v5h5a1 1 0 110 2h-5v5a1 1 0 11-2 0v-5H4a1 1 0 110-2h5V4a1 1 0 011-1z"
                    clipRule="evenodd"
                  />
                </svg>
                New Message
              </Link>
            )}
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
              <div className="p-6 text-center text-gray-500">
                {searchQuery ? 'No messages matching your search criteria.' : 'No messages available.'}
              </div>
            ) : (
              <div className="divide-y divide-gray-200">
                {filteredMessages.map((message) => (
                  <div
                    key={message.id}
                    className="p-6 hover:bg-gray-50 transition duration-150 ease-in-out"
                  >
                    <div className="flex flex-col sm:flex-row sm:justify-between sm:items-start mb-2">
                      <h3 className="text-lg font-medium text-gray-900">{message.subject}</h3>
                      <span className="text-sm text-gray-500 mt-1 sm:mt-0">
                        {formatDate(message.sentAt)}
                      </span>
                    </div>
                    
                    <p className="text-sm text-gray-500 mb-4">
                      {userIsManager ? `To: ${message.employeeName}` : `From: ${message.employeeName}`}
                    </p>
                    
                    <div className="prose prose-sm max-w-none text-gray-700 whitespace-pre-wrap">
                      {message.content}
                    </div>
                    
                    <div className="mt-4 flex justify-between items-center">
                      <span
                        className={`px-2 inline-flex text-xs leading-5 font-semibold rounded-full ${
                          message.status === 'SENT' ? 'bg-green-100 text-green-800' : 'bg-red-100 text-red-800'
                        }`}
                      >
                        {message.status}
                      </span>
                      
                      {userIsManager && (
                        <Link
                          href={`/dashboard/messaging?employeeId=${message.employeeId}`}
                          className="text-primary hover:text-primary-dark font-medium text-sm"
                        >
                          Reply
                        </Link>
                      )}
                    </div>
                  </div>
                ))}
              </div>
            )}
          </div>
        </div>
      </Layout>
    </ProtectedRoute>
  );
};

export default MessagesPage;