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
  onCancel,
}) => {
  const [formData, setFormData] = useState<DepartmentFormData>({
    name: '',
    budget: 0,
    budgetType: 'MONTHLY',
  });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  // Initialize form with department data if editing
  useEffect(() => {
    if (department) {
      setFormData({
        name: department.name,
        budget: department.budget,
        budgetType: department.budgetType,
      });
    }
  }, [department]);

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
    const { name, value, type } = e.target;
    
    setFormData(prev => ({
      ...prev,
      [name]: type === 'number' ? Number(value) : value,
    }));
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    setError('');

    try {
      // Validate form data
      if (formData.budget < 0) {
        throw new Error('Budget cannot be negative');
      }

      await onSubmit(formData);
    } catch (err: any) {
      setError(err.message || 'Failed to save department data');
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
          <label htmlFor="name" className="label">
            Department Name
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
          <label htmlFor="budget" className="label">
            Budget Amount
          </label>
          <input
            id="budget"
            name="budget"
            type="number"
            step="0.01"
            min="0"
            required
            value={formData.budget}
            onChange={handleChange}
            className="input"
          />
        </div>

        <div>
          <label htmlFor="budgetType" className="label">
            Budget Type
          </label>
          <select
            id="budgetType"
            name="budgetType"
            required
            value={formData.budgetType}
            onChange={handleChange}
            className="input"
          >
            <option value="MONTHLY">Monthly</option>
            <option value="YEARLY">Yearly</option>
          </select>
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
          {loading ? 'Saving...' : 'Save Department'}
        </button>
      </div>
    </form>
  );
};

export default DepartmentForm;