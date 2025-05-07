import { useState, useEffect } from 'react';

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
  const [formData, setFormData] = useState<{
    employeeId: number;
    subject: string;
    content: string;
  }>({
    employeeId: initialEmployeeId || (employees.length > 0 ? employees[0].id : 0),
    subject: '',
    content: ''
  });

  useEffect(() => {
    if (initialEmployeeId) {
      setFormData(prev => ({
        ...prev,
        employeeId: initialEmployeeId
      }));
    }
  }, [initialEmployeeId]);

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement | HTMLTextAreaElement>) => {
    const { name, value } = e.target;
    
    setFormData(prev => ({
      ...prev,
      [name]: name === 'employeeId' ? parseInt(value) : value
    }));
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      await onSubmit(formData);
    } catch (error) {
      console.error('Error submitting message form:', error);
    }
  };

  const selectedEmployee = employees.find(emp => emp.id === formData.employeeId);

  if (employees.length === 0) {
    return (
      <div className="fixed inset-0 bg-gray-500 bg-opacity-75 flex items-center justify-center z-50">
        <div className="bg-white rounded-lg shadow-xl max-w-md w-full p-6">
          <h3 className="text-lg font-medium text-gray-900 mb-4">Error</h3>
          <p className="text-red-500">No active employees available.</p>
          <div className="mt-6 flex justify-end">
            <button
              type="button"
              onClick={onCancel}
              className="inline-flex items-center px-4 py-2 border border-gray-300 rounded-md shadow-sm text-sm font-medium text-gray-700 bg-white hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500"
            >
              Close
            </button>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="fixed inset-0 bg-gray-500 bg-opacity-75 flex items-center justify-center z-50">
      <div className="bg-white rounded-lg shadow-xl max-w-2xl w-full p-6">
        <h3 className="text-lg font-medium text-gray-900 mb-4">Compose Message</h3>
        
        <form onSubmit={handleSubmit}>
          <div className="mb-4">
            <label htmlFor="employeeId" className="block text-sm font-medium text-gray-700 mb-1">
              Recipient
            </label>
            <select
              id="employeeId"
              name="employeeId"
              value={formData.employeeId}
              onChange={handleChange}
              className="mt-1 block w-full pl-3 pr-10 py-2 text-base border-gray-300 focus:outline-none focus:ring-blue-500 focus:border-blue-500 sm:text-sm rounded-md"
              required
              disabled={initialEmployeeId !== null}
            >
              {employees.map(employee => (
                <option key={employee.id} value={employee.id}>
                  {employee.name} - {employee.departmentName}
                </option>
              ))}
            </select>
            {selectedEmployee && (
              <p className="mt-1 text-xs text-gray-500">
                Email: {selectedEmployee.email}
              </p>
            )}
          </div>

          <div className="mb-4">
            <label htmlFor="subject" className="block text-sm font-medium text-gray-700 mb-1">
              Subject
            </label>
            <input
              type="text"
              id="subject"
              name="subject"
              value={formData.subject}
              onChange={handleChange}
              className="mt-1 block w-full border border-gray-300 rounded-md shadow-sm py-2 px-3 focus:outline-none focus:ring-blue-500 focus:border-blue-500 sm:text-sm"
              required
            />
          </div>

          <div className="mb-4">
            <label htmlFor="content" className="block text-sm font-medium text-gray-700 mb-1">
              Message
            </label>
            <textarea
              id="content"
              name="content"
              rows={6}
              value={formData.content}
              onChange={handleChange}
              className="mt-1 block w-full border border-gray-300 rounded-md shadow-sm py-2 px-3 focus:outline-none focus:ring-blue-500 focus:border-blue-500 sm:text-sm"
              required
            ></textarea>
          </div>

          <div className="mt-6 flex justify-end space-x-3">
            <button
              type="button"
              onClick={onCancel}
              className="inline-flex items-center px-4 py-2 border border-gray-300 rounded-md shadow-sm text-sm font-medium text-gray-700 bg-white hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500"
            >
              Cancel
            </button>
            <button
              type="submit"
              className="inline-flex items-center px-4 py-2 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-blue-600 hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500"
            >
              Send
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default MessageForm;
