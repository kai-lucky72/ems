import { useState, useEffect } from 'react';
import { MessageFormData } from '@/types';

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
  onCancel,
}) => {
  const [formData, setFormData] = useState<MessageFormData>({
    employeeId: initialEmployeeId || 0,
    subject: '',
    content: '',
  });
  
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [wordCount, setWordCount] = useState(0);

  // Initialize form with employee ID if provided
  useEffect(() => {
    if (initialEmployeeId) {
      setFormData(prev => ({
        ...prev,
        employeeId: initialEmployeeId,
      }));
    } else if (employees.length > 0) {
      setFormData(prev => ({
        ...prev,
        employeeId: employees[0].id,
      }));
    }
  }, [initialEmployeeId, employees]);

  // Update word count when content changes
  useEffect(() => {
    const words = formData.content.trim().split(/\s+/);
    setWordCount(formData.content.trim() === '' ? 0 : words.length);
  }, [formData.content]);

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement | HTMLTextAreaElement>) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: name === 'employeeId' ? parseInt(value, 10) : value,
    }));
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    setError('');

    try {
      // Validate form data
      if (formData.employeeId === 0) {
        throw new Error('Please select an employee');
      }

      if (!formData.subject.trim()) {
        throw new Error('Subject is required');
      }

      if (!formData.content.trim()) {
        throw new Error('Message content is required');
      }

      await onSubmit(formData);
    } catch (err: any) {
      setError(err.message || 'Failed to send message');
    } finally {
      setLoading(false);
    }
  };

  // Get the selected employee's email for display
  const getEmployeeEmail = () => {
    const employee = employees.find(emp => emp.id === formData.employeeId);
    return employee ? employee.email : '';
  };

  return (
    <form onSubmit={handleSubmit} className="space-y-6">
      {error && (
        <div className="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded">
          {error}
        </div>
      )}

      <div className="space-y-4">
        <div>
          <label htmlFor="employeeId" className="label">
            Recipient
          </label>
          <select
            id="employeeId"
            name="employeeId"
            value={formData.employeeId}
            onChange={handleChange}
            className="input"
            required
            disabled={initialEmployeeId !== null}
          >
            <option value="">Select Employee</option>
            {employees.map((employee) => (
              <option key={employee.id} value={employee.id}>
                {employee.name} - {employee.departmentName}
              </option>
            ))}
          </select>
          {formData.employeeId > 0 && (
            <div className="mt-1 text-sm text-gray-500">
              Email: {getEmployeeEmail()}
            </div>
          )}
        </div>

        <div>
          <label htmlFor="subject" className="label">
            Subject
          </label>
          <input
            id="subject"
            name="subject"
            type="text"
            required
            value={formData.subject}
            onChange={handleChange}
            className="input"
            placeholder="Message subject"
          />
        </div>

        <div>
          <label htmlFor="content" className="label">
            Message
          </label>
          <textarea
            id="content"
            name="content"
            required
            value={formData.content}
            onChange={handleChange}
            rows={6}
            className="input"
            placeholder="Type your message here..."
          ></textarea>
          <div className="mt-1 text-xs text-gray-500 flex justify-end">
            {wordCount} word{wordCount !== 1 ? 's' : ''}
          </div>
        </div>
      </div>

      <div className="flex justify-end space-x-3">
        <button
          type="button"
          onClick={onCancel}
          className="btn border border-gray-300 bg-white text-gray-700 hover:bg-gray-50"
        >
          Cancel
        </button>
        <button
          type="submit"
          disabled={loading}
          className="btn btn-primary"
        >
          {loading ? 'Sending...' : 'Send Message'}
        </button>
      </div>
    </form>
  );
};

export default MessageForm;