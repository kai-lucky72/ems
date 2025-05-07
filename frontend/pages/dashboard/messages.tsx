import { useState, useEffect } from 'react';
import { NextPage } from 'next';
import Layout from '@/components/Layout';
import ProtectedRoute from '@/components/auth/ProtectedRoute';
import { getUser } from '@/lib/auth';
import { Message } from '@/types';

const MessagesPage: NextPage = () => {
  const [messages, setMessages] = useState<Message[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [selectedMessage, setSelectedMessage] = useState<Message | null>(null);
  const [replyText, setReplyText] = useState('');

  useEffect(() => {
    // Simulate API call
    setTimeout(() => {
      try {
        // Mock messages data
        const mockMessages: Message[] = [
          {
            id: 1,
            employeeId: 1,
            employeeName: 'John Doe',
            subject: 'Welcome to the Engineering Team',
            content: 'Hello John,\n\nWe are excited to welcome you to our Engineering team! Please let me know if you have any questions about your role or the company.\n\nBest regards,\nEmily Johnson\nEngineering Manager',
            sentAt: '2023-05-15T09:30:00Z',
            status: 'SENT'
          },
          {
            id: 2,
            employeeId: 1,
            employeeName: 'John Doe',
            subject: 'Annual Performance Review',
            content: 'Dear John,\n\nThis is a reminder that your annual performance review is scheduled for July 15th at 10:00 AM. Please prepare a short summary of your achievements over the past year.\n\nRegards,\nHR Department',
            sentAt: '2023-06-20T14:15:00Z',
            status: 'SENT'
          },
          {
            id: 3,
            employeeId: 1,
            employeeName: 'John Doe',
            subject: 'Company-wide Meeting',
            content: 'Hi everyone,\n\nWe will be having a company-wide meeting next Friday at 2:00 PM to discuss quarterly results and future plans. Attendance is mandatory.\n\nThanks,\nManagement Team',
            sentAt: '2023-07-01T11:45:00Z',
            status: 'SENT'
          }
        ];
        
        setMessages(mockMessages);
        setLoading(false);
      } catch (err) {
        console.error('Error fetching messages:', err);
        setError('Failed to load messages. Please try again later.');
        setLoading(false);
      }
    }, 1000);
  }, []);

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

  const handleReply = () => {
    if (!selectedMessage || !replyText.trim()) return;
    
    // In a real app, this would send the reply to the backend
    alert(`Reply to "${selectedMessage.subject}" sent.`);
    setReplyText('');
    setSelectedMessage(null);
  };

  return (
    <ProtectedRoute>
      <Layout>
        <div className="space-y-6">
          <div className="flex items-center justify-between">
            <h1 className="text-2xl font-bold">My Messages</h1>
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
            <div className="grid grid-cols-1 md:grid-cols-3 gap-6 h-[calc(100vh-200px)]">
              {/* Message list */}
              <div className="bg-white rounded-lg shadow overflow-hidden">
                <div className="px-4 py-3 border-b border-gray-200 bg-gray-50">
                  <h2 className="text-lg font-semibold">Inbox</h2>
                </div>
                <div className="divide-y divide-gray-200 max-h-[calc(100vh-270px)] overflow-y-auto">
                  {messages.length === 0 ? (
                    <div className="p-6 text-center text-gray-500">
                      No messages to display
                    </div>
                  ) : (
                    messages.map((message) => (
                      <div
                        key={message.id}
                        onClick={() => setSelectedMessage(message)}
                        className={`p-4 cursor-pointer hover:bg-gray-50 transition-colors ${
                          selectedMessage?.id === message.id ? 'bg-gray-100' : ''
                        }`}
                      >
                        <div className="flex justify-between mb-1">
                          <h3 className="font-medium truncate">{message.subject}</h3>
                          <span className="text-xs text-gray-500 whitespace-nowrap ml-2">{formatDate(message.sentAt).split(',')[0]}</span>
                        </div>
                        <p className="text-sm text-gray-500 line-clamp-2">
                          {message.content.split('\n')[0]}
                        </p>
                      </div>
                    ))
                  )}
                </div>
              </div>
              
              {/* Message content */}
              <div className="bg-white rounded-lg shadow overflow-hidden md:col-span-2">
                {selectedMessage ? (
                  <div className="flex flex-col h-full">
                    <div className="px-6 py-4 border-b border-gray-200">
                      <h2 className="text-xl font-semibold">{selectedMessage.subject}</h2>
                      <div className="flex justify-between mt-2 text-sm text-gray-500">
                        <span>From: HR</span>
                        <span>{formatDate(selectedMessage.sentAt)}</span>
                      </div>
                    </div>
                    <div className="p-6 flex-grow overflow-y-auto">
                      <div className="whitespace-pre-line">
                        {selectedMessage.content}
                      </div>
                    </div>
                    <div className="p-4 border-t border-gray-200">
                      <div className="mb-2">
                        <textarea
                          className="w-full border border-gray-300 rounded-md px-3 py-2 focus:outline-none focus:ring-2 focus:ring-primary focus:border-transparent"
                          rows={3}
                          placeholder="Write your reply..."
                          value={replyText}
                          onChange={(e) => setReplyText(e.target.value)}
                        ></textarea>
                      </div>
                      <div className="flex justify-end space-x-2">
                        <button
                          className="px-4 py-2 border border-gray-300 rounded-md text-sm font-medium text-gray-700 hover:bg-gray-50"
                          onClick={() => setSelectedMessage(null)}
                        >
                          Cancel
                        </button>
                        <button
                          className="px-4 py-2 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-primary hover:bg-primary-dark"
                          onClick={handleReply}
                          disabled={!replyText.trim()}
                        >
                          Send Reply
                        </button>
                      </div>
                    </div>
                  </div>
                ) : (
                  <div className="flex items-center justify-center h-full text-gray-500 p-6">
                    Select a message to view its content
                  </div>
                )}
              </div>
            </div>
          )}
        </div>
      </Layout>
    </ProtectedRoute>
  );
};

export default MessagesPage;