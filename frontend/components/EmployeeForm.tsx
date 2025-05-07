import { useState, useEffect } from 'react';
import { Employee, EmployeeFormData } from '@/types';

interface Department {
  id: number;
  name: string;
}

interface EmployeeFormProps {
  employee: Employee | null;
  departments: Department[];
  onSubmit: (formData: EmployeeFormData) => Promise<void>;
  onCancel: () => void;
}

const EmployeeForm: React.FC<EmployeeFormProps> = ({
  employee,
  departments,
  onSubmit,
  onCancel,
}) => {
  const [formData, setFormData] = useState<EmployeeFormData>({
    name: '',
    email: '',
    phone: '',
    role: '',
    departmentId: 0,
    contractType: 'FULL_TIME',
    startDate: '',
    endDate: null,
    isActive: true,
  });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  // Initialize form with employee data if editing
  useEffect(() => {
    if (employee) {
      setFormData({
        name: employee.name,
        email: employee.email,
        phone: employee.phone,
        role: employee.role,
        departmentId: employee.departmentId,
        contractType: employee.contractType,
        startDate: employee.startDate.split('T')[0], // Format date for input
        endDate: employee.endDate ? employee.endDate.split('T')[0] : null,
        isActive: employee.isActive,
      });
    } else if (departments.length > 0) {
      // Set default department if creating new employee
      setFormData(prev => ({
        ...prev,
        departmentId: departments[0].id,
      }));
    }
  }, [employee, departments]);

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
    const { name, value, type } = e.target;
    const checked = (e.target as HTMLInputElement).checked;

    setFormData(prev => ({
      ...prev,
      [name]: type === 'checkbox' ? checked : value,
    }));
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    setError('');

    try {
      const submissionData: EmployeeFormData = {
        name: formData.name,
        email: formData.email,
        phone: formData.phone,
        role: formData.role,
        departmentId: Number(formData.departmentId),
        contractType: formData.contractType as 'FULL_TIME' | 'PART_TIME' | 'REMOTE',
        startDate: formData.startDate,
        endDate: formData.endDate || null,
        isActive: formData.isActive
      };

      await onSubmit(submissionData);
    } catch (err: any) {
      setError(err.message || 'Failed to save employee data');
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

      <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
        <div>
          <label htmlFor="name" className="label">
            Full Name
          </label>
          <input
            id="name"
            name="name"
            type="text"
            required
            value={formData.name}
            onChange={handleChange}
            className="input"
          />
        </div>

        <div>
          <label htmlFor="email" className="label">
            Email
          </label>
          <input
            id="email"
            name="email"
            type="email"
            required
            value={formData.email}
            onChange={handleChange}
            className="input"
          />
        </div>

        <div>
          <label htmlFor="phone" className="label">
            Phone
          </label>
          <input
            id="phone"
            name="phone"
            type="tel"
            required
            value={formData.phone}
            onChange={handleChange}
            className="input"
          />
        </div>

        <div>
          <label htmlFor="role" className="label">
            Job Role
          </label>
          <input
            id="role"
            name="role"
            type="text"
            required
            value={formData.role}
            onChange={handleChange}
            className="input"
          />
        </div>

        <div>
          <label htmlFor="departmentId" className="label">
            Department
          </label>
          <select
            id="departmentId"
            name="departmentId"
            required
            value={formData.departmentId}
            onChange={handleChange}
            className="input"
          >
            <option value="">Select Department</option>
            {departments.map((dept) => (
              <option key={dept.id} value={dept.id}>
                {dept.name}
              </option>
            ))}
          </select>
        </div>

        <div>
          <label htmlFor="contractType" className="label">
            Contract Type
          </label>
          <select
            id="contractType"
            name="contractType"
            required
            value={formData.contractType}
            onChange={handleChange}
            className="input"
          >
            <option value="FULL_TIME">Full Time</option>
            <option value="PART_TIME">Part Time</option>
            <option value="REMOTE">Remote</option>
          </select>
        </div>

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
          />
        </div>

        <div>
          <label htmlFor="endDate" className="label">
            End Date (if applicable)
          </label>
          <input
            id="endDate"
            name="endDate"
            type="date"
            value={formData.endDate || ''}
            onChange={handleChange}
            className="input"
          />
        </div>

        <div className="md:col-span-2">
          <div className="flex items-center">
            <input
              id="isActive"
              name="isActive"
              type="checkbox"
              checked={formData.isActive}
              onChange={handleChange}
              className="h-4 w-4 text-primary focus:ring-primary border-gray-300 rounded"
            />
            <label htmlFor="isActive" className="ml-2 block text-sm text-gray-700">
              Active Employee
            </label>
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
          {loading ? 'Saving...' : 'Save Employee'}
        </button>
      </div>
    </form>
  );
};

export default EmployeeForm;