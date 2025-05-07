import { useState } from 'react';

interface Employee {
  id: number;
  name: string;
  email: string;
  departmentName: string;
}

interface MessageFormProps {
  employees: Employee[];
  initialEmployeeId: number | null;
  onSubmit: (formData: { 
    employeeId: number;
    subject: string;
    content: string;
  }) => Promise<void>;
  onCancel: () => void;
}

const MessageForm: React.FC<MessageFormProps> = ({ 
  employees, 
  initialEmployeeId, 
  onSubmit, 
  onCancel 
}) => {
  const [formData, setFormData] = useState({
    employeeId: initialEmployeeId || (employees.length > 0 ? employees[0].id : 0),
    subject: '',
    content: '',
  });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const handleChange = (
    e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement | HTMLTextAreaElement>
  ) => {
    const { name, value } = e.target;
    
    setFormData(prev => ({
      ...prev,
      [name]: name === 'employeeId' ? Number(value) : value
    }));
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    
    if (!formData.subject.trim()) {
      setError('Subject is required');
      return;
    }
    
    if (!formData.content.trim()) {
      setError('Message content is required');
      return;
    }
    
    setLoading(true);
    setError('');

    try {
      await onSubmit(formData);
    } catch (err: any) {
      setError(err.message || 'Failed to send message. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  // Find selected employee details for display
  const selectedEmployee = employees.find(emp => emp.id === formData.employeeId);

  return (
    <form onSubmit={handleSubmit} className="space-y-5">
      {error && (
        <div className="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded">
          {error}
        </div>
      )}

      <div>
        <label htmlFor="employeeId" className="block text-sm font-medium text-gray-700">
          Recipient
        </label>
        <div className="mt-1">
          <select
            id="employeeId"
            name="employeeId"
            value={formData.employeeId}
            onChange={handleChange}
            className="shadow-sm focus:ring-primary focus:border-primary block w-full sm:text-sm border-gray-300 rounded-md"
            required
            disabled={initialEmployeeId !== null}
          >
            {employees.map(employee => (
              <option key={employee.id} value={employee.id}>
                {employee.name} ({employee.departmentName})
              </option>
            ))}
          </select>
        </div>
        {selectedEmployee && (
          <p className="mt-1 text-sm text-gray-500">
            Email: {selectedEmployee.email}
          </p>
        )}
      </div>

      <div>
        <label htmlFor="subject" className="block text-sm font-medium text-gray-700">
          Subject
        </label>
        <div className="mt-1">
          <input
            type="text"
            id="subject"
            name="subject"
            value={formData.subject}
            onChange={handleChange}
            className="shadow-sm focus:ring-primary focus:border-primary block w-full sm:text-sm border-gray-300 rounded-md"
            required
          />
        </div>
      </div>

      <div>
        <label htmlFor="content" className="block text-sm font-medium text-gray-700">
          Message
        </label>
        <div className="mt-1">
          <textarea
            id="content"
            name="content"
            rows={8}
            value={formData.content}
            onChange={handleChange}
            className="shadow-sm focus:ring-primary focus:border-primary block w-full sm:text-sm border-gray-300 rounded-md"
            required
          />
        </div>
      </div>

      <div className="flex justify-end space-x-3 pt-4">
        <button
          type="button"
          onClick={onCancel}
          className="px-4 py-2 border border-gray-300 rounded-md shadow-sm text-sm font-medium text-gray-700 bg-white hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-primary"
        >
          Cancel
        </button>
        <button
          type="submit"
          disabled={loading}
          className="px-4 py-2 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-primary hover:bg-primary-dark focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-primary"
        >
          {loading ? 'Sending...' : 'Send Message'}
        </button>
      </div>
    </form>
  );
};

export default MessageForm;