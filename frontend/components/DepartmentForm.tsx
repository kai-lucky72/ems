import { useState, useEffect } from 'react';
import { Department, DepartmentFormData } from '@/types';

interface DepartmentFormProps {
  department: Department | null;
  onSubmit: (formData: DepartmentFormData) => Promise<void>;
  onCancel: () => void;
}

const DepartmentForm: React.FC<DepartmentFormProps> = ({
  department,
  onSubmit,
  onCancel
}) => {
  const [formData, setFormData] = useState<DepartmentFormData>({
    name: '',
    budget: 0,
    budgetType: 'MONTHLY'
  });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  // Initialize form with department data if editing
  useEffect(() => {
    if (department) {
      setFormData({
        name: department.name,
        budget: department.budget,
        budgetType: department.budgetType
      });
    }
  }, [department]);

  const handleChange = (
    e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>
  ) => {
    const { name, value } = e.target;
    
    setFormData(prev => ({
      ...prev,
      [name]: name === 'budget' ? Number(value) : value
    }));
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    setError('');

    try {
      await onSubmit(formData);
    } catch (err: any) {
      setError(err.message || 'Failed to save department. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <form onSubmit={handleSubmit} className="space-y-4">
      {error && (
        <div className="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded">
          {error}
        </div>
      )}

      <div>
        <label htmlFor="name" className="block text-sm font-medium text-gray-700">
          Department Name
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
        <label htmlFor="budget" className="block text-sm font-medium text-gray-700">
          Budget Amount
        </label>
        <div className="mt-1">
          <input
            type="number"
            name="budget"
            id="budget"
            required
            min={0}
            step={1000}
            value={formData.budget}
            onChange={handleChange}
            className="shadow-sm focus:ring-primary focus:border-primary block w-full sm:text-sm border-gray-300 rounded-md"
          />
        </div>
      </div>

      <div>
        <label htmlFor="budgetType" className="block text-sm font-medium text-gray-700">
          Budget Type
        </label>
        <div className="mt-1">
          <select
            name="budgetType"
            id="budgetType"
            required
            value={formData.budgetType}
            onChange={handleChange}
            className="shadow-sm focus:ring-primary focus:border-primary block w-full sm:text-sm border-gray-300 rounded-md"
          >
            <option value="MONTHLY">Monthly</option>
            <option value="YEARLY">Yearly</option>
          </select>
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
          {loading ? 'Saving...' : department ? 'Update Department' : 'Create Department'}
        </button>
      </div>
    </form>
  );
};

export default DepartmentForm;