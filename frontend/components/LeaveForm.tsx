import { useState, useEffect } from 'react';
import { LeaveFormData } from '@/types';

interface Employee {
  id: number;
  name: string;
  departmentName: string;
}

interface LeaveFormProps {
  employees: Employee[];
  onSubmit: (formData: { 
    employeeId: number;
    startDate: string;
    endDate: string;
    reason: string;
  }) => Promise<void>;
  onCancel: () => void;
}

const LeaveForm: React.FC<LeaveFormProps> = ({
  employees,
  onSubmit,
  onCancel,
}) => {
  const [formData, setFormData] = useState<LeaveFormData>({
    employeeId: 0,
    startDate: '',
    endDate: '',
    reason: '',
  });
  
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [leaveDuration, setLeaveDuration] = useState(0);

  // Initialize form with first employee if available
  useEffect(() => {
    if (employees.length > 0) {
      setFormData(prev => ({
        ...prev,
        employeeId: employees[0].id,
      }));
    }
  }, [employees]);

  // Calculate leave duration when dates change
  useEffect(() => {
    if (formData.startDate && formData.endDate) {
      const start = new Date(formData.startDate);
      const end = new Date(formData.endDate);
      
      // Calculate the difference in days
      const diffTime = Math.abs(end.getTime() - start.getTime());
      const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24)) + 1; // +1 to include both start and end dates
      
      setLeaveDuration(diffDays);
    } else {
      setLeaveDuration(0);
    }
  }, [formData.startDate, formData.endDate]);

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

      if (!formData.startDate) {
        throw new Error('Start date is required');
      }

      if (!formData.endDate) {
        throw new Error('End date is required');
      }

      const startDate = new Date(formData.startDate);
      const endDate = new Date(formData.endDate);

      if (endDate < startDate) {
        throw new Error('End date cannot be before start date');
      }

      if (!formData.reason.trim()) {
        throw new Error('Reason for leave is required');
      }

      await onSubmit(formData);
    } catch (err: any) {
      setError(err.message || 'Failed to save leave request');
    } finally {
      setLoading(false);
    }
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
            Employee
          </label>
          <select
            id="employeeId"
            name="employeeId"
            value={formData.employeeId}
            onChange={handleChange}
            className="input"
            required
          >
            <option value="">Select Employee</option>
            {employees.map((employee) => (
              <option key={employee.id} value={employee.id}>
                {employee.name} - {employee.departmentName}
              </option>
            ))}
          </select>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          <div>
            <label htmlFor="startDate" className="label">
              Start Date
            </label>
            <input
              id="startDate"
              name="startDate"
              type="date"
              required
              value={formData.startDate}
              onChange={handleChange}
              className="input"
              min={new Date().toISOString().split('T')[0]} // Prevent selecting past dates
            />
          </div>

          <div>
            <label htmlFor="endDate" className="label">
              End Date
            </label>
            <input
              id="endDate"
              name="endDate"
              type="date"
              required
              value={formData.endDate}
              onChange={handleChange}
              className="input"
              min={formData.startDate || new Date().toISOString().split('T')[0]}
            />
          </div>
        </div>

        {leaveDuration > 0 && (
          <div className="bg-blue-50 p-3 rounded-lg border border-blue-100">
            <span className="text-sm font-medium">
              Duration: <span className="text-blue-700">{leaveDuration} day{leaveDuration !== 1 ? 's' : ''}</span>
            </span>
          </div>
        )}

        <div>
          <label htmlFor="reason" className="label">
            Reason for Leave
          </label>
          <textarea
            id="reason"
            name="reason"
            required
            value={formData.reason}
            onChange={handleChange}
            rows={4}
            className="input"
            placeholder="Please provide details about your leave request..."
          ></textarea>
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
          {loading ? 'Submitting...' : 'Submit Leave Request'}
        </button>
      </div>
    </form>
  );
};

export default LeaveForm;