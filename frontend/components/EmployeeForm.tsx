import { useState, useEffect } from 'react';
import { Employee, Department, EmployeeFormData } from '@/types';

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
  onCancel
}) => {
  const [formData, setFormData] = useState<EmployeeFormData>({
    name: '',
    email: '',
    phone: '',
    role: '',
    departmentId: departments.length > 0 ? departments[0].id : 0,
    contractType: 'FULL_TIME',
    startDate: new Date().toISOString().split('T')[0],
    endDate: null,
    isActive: true
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
        startDate: employee.startDate.split('T')[0],
        endDate: employee.endDate ? employee.endDate.split('T')[0] : null,
        isActive: employee.isActive
      });
    }
  }, [employee]);

  const handleChange = (
    e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement | HTMLTextAreaElement>
  ) => {
    const { name, value, type } = e.target;
    
    setFormData(prev => ({
      ...prev,
      [name]: type === 'checkbox' 
        ? (e.target as HTMLInputElement).checked 
        : name === 'departmentId' 
          ? Number(value)
          : name === 'endDate' && value === ''
            ? null
            : value
    }));
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    setError('');

    try {
      await onSubmit(formData);
    } catch (err: any) {
      setError(err.message || 'Failed to save employee. Please try again.');
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
        {/* Personal Information */}
        <div className="space-y-4">
          <h3 className="text-lg font-medium text-gray-800">Personal Information</h3>
          
          <div>
            <label htmlFor="name" className="block text-sm font-medium text-gray-700">
              Full Name
            </label>
            <div className="mt-1">
              <input
                type="text"
                name="name"
                id="name"
                required
                value={formData.name}
                onChange={handleChange}
                className="shadow-sm focus:ring-primary focus:border-primary block w-full sm:text-sm border-gray-300 rounded-md"
              />
            </div>
          </div>

          <div>
            <label htmlFor="email" className="block text-sm font-medium text-gray-700">
              Email Address
            </label>
            <div className="mt-1">
              <input
                type="email"
                name="email"
                id="email"
                required
                value={formData.email}
                onChange={handleChange}
                className="shadow-sm focus:ring-primary focus:border-primary block w-full sm:text-sm border-gray-300 rounded-md"
              />
            </div>
          </div>

          <div>
            <label htmlFor="phone" className="block text-sm font-medium text-gray-700">
              Phone Number
            </label>
            <div className="mt-1">
              <input
                type="tel"
                name="phone"
                id="phone"
                required
                value={formData.phone}
                onChange={handleChange}
                className="shadow-sm focus:ring-primary focus:border-primary block w-full sm:text-sm border-gray-300 rounded-md"
              />
            </div>
          </div>
          
          <div>
            <label htmlFor="role" className="block text-sm font-medium text-gray-700">
              Job Title
            </label>
            <div className="mt-1">
              <input
                type="text"
                name="role"
                id="role"
                required
                value={formData.role}
                onChange={handleChange}
                className="shadow-sm focus:ring-primary focus:border-primary block w-full sm:text-sm border-gray-300 rounded-md"
              />
            </div>
          </div>
        </div>

        {/* Employment Details */}
        <div className="space-y-4">
          <h3 className="text-lg font-medium text-gray-800">Employment Details</h3>
          
          <div>
            <label htmlFor="departmentId" className="block text-sm font-medium text-gray-700">
              Department
            </label>
            <div className="mt-1">
              <select
                name="departmentId"
                id="departmentId"
                required
                value={formData.departmentId}
                onChange={handleChange}
                className="shadow-sm focus:ring-primary focus:border-primary block w-full sm:text-sm border-gray-300 rounded-md"
              >
                {departments.map((dept) => (
                  <option key={dept.id} value={dept.id}>
                    {dept.name}
                  </option>
                ))}
              </select>
            </div>
          </div>

          <div>
            <label htmlFor="contractType" className="block text-sm font-medium text-gray-700">
              Contract Type
            </label>
            <div className="mt-1">
              <select
                name="contractType"
                id="contractType"
                required
                value={formData.contractType}
                onChange={handleChange}
                className="shadow-sm focus:ring-primary focus:border-primary block w-full sm:text-sm border-gray-300 rounded-md"
              >
                <option value="FULL_TIME">Full Time</option>
                <option value="PART_TIME">Part Time</option>
                <option value="REMOTE">Remote</option>
              </select>
            </div>
          </div>

          <div>
            <label htmlFor="startDate" className="block text-sm font-medium text-gray-700">
              Start Date
            </label>
            <div className="mt-1">
              <input
                type="date"
                name="startDate"
                id="startDate"
                required
                value={formData.startDate}
                onChange={handleChange}
                className="shadow-sm focus:ring-primary focus:border-primary block w-full sm:text-sm border-gray-300 rounded-md"
              />
            </div>
          </div>

          <div>
            <label htmlFor="endDate" className="block text-sm font-medium text-gray-700">
              End Date (If applicable)
            </label>
            <div className="mt-1">
              <input
                type="date"
                name="endDate"
                id="endDate"
                value={formData.endDate || ''}
                onChange={handleChange}
                min={formData.startDate}
                className="shadow-sm focus:ring-primary focus:border-primary block w-full sm:text-sm border-gray-300 rounded-md"
              />
            </div>
          </div>

          <div className="flex items-center mt-4">
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
          {loading ? 'Saving...' : employee ? 'Update Employee' : 'Add Employee'}
        </button>
      </div>
    </form>
  );
};

export default EmployeeForm;