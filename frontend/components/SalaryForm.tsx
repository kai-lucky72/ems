import { useState, useEffect } from 'react';
import { Salary, Deduction } from '@/types';

interface Employee {
  id: number;
  name: string;
  departmentName: string;
}

interface SalaryFormProps {
  salary: Salary | null;
  employeeId: number | null;
  employees: Employee[];
  onSubmit: (formData: { employeeId: number; grossSalary: number; deductions: Deduction[] }) => Promise<void>;
  onCancel: () => void;
}

const SalaryForm: React.FC<SalaryFormProps> = ({
  salary,
  employeeId: initialEmployeeId,
  employees,
  onSubmit,
  onCancel,
}) => {
  const [formData, setFormData] = useState({
    employeeId: initialEmployeeId || 0,
    grossSalary: 0,
  });
  
  const [deductions, setDeductions] = useState<Deduction[]>([]);
  const [newDeduction, setNewDeduction] = useState<Deduction>({
    type: 'TAX',
    name: '',
    value: 0,
    isPercentage: true,
  });
  
  const [netSalary, setNetSalary] = useState(0);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  // Initialize form with salary data if editing
  useEffect(() => {
    if (salary) {
      setFormData({
        employeeId: salary.employeeId,
        grossSalary: salary.grossSalary,
      });
      setDeductions(salary.deductions || []);
    } else if (initialEmployeeId) {
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
  }, [salary, initialEmployeeId, employees]);

  // Calculate net salary whenever gross salary or deductions change
  useEffect(() => {
    let totalDeductionAmount = 0;
    
    deductions.forEach(deduction => {
      if (deduction.isPercentage) {
        totalDeductionAmount += (formData.grossSalary * deduction.value) / 100;
      } else {
        totalDeductionAmount += deduction.value;
      }
    });
    
    setNetSalary(formData.grossSalary - totalDeductionAmount);
  }, [formData.grossSalary, deductions]);

  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: name === 'employeeId' ? Number(value) : parseFloat(value),
    }));
  };

  const handleNewDeductionChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
    const { name, value, type } = e.target;
    const checked = type === 'checkbox' ? (e.target as HTMLInputElement).checked : undefined;
    
    setNewDeduction(prev => ({
      ...prev,
      [name]: type === 'checkbox' ? checked : 
              name === 'value' ? parseFloat(value) : value,
    }));
  };

  const handleAddDeduction = () => {
    if (!newDeduction.name) {
      setError('Deduction name is required');
      return;
    }

    if (newDeduction.value <= 0) {
      setError('Deduction value must be greater than zero');
      return;
    }

    setDeductions(prev => [...prev, newDeduction]);
    setNewDeduction({
      type: 'TAX',
      name: '',
      value: 0,
      isPercentage: true,
    });
    setError('');
  };

  const handleRemoveDeduction = (index: number) => {
    setDeductions(prev => prev.filter((_, i) => i !== index));
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    setError('');

    try {
      // Validate form data
      if (formData.grossSalary <= 0) {
        throw new Error('Gross salary must be greater than zero');
      }
      
      if (formData.employeeId === 0) {
        throw new Error('Please select an employee');
      }

      await onSubmit({
        employeeId: formData.employeeId,
        grossSalary: formData.grossSalary,
        deductions: deductions,
      });
    } catch (err: any) {
      setError(err.message || 'Failed to save salary data');
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
        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          <div>
            <label htmlFor="employeeId" className="label">
              Employee
            </label>
            <select
              id="employeeId"
              name="employeeId"
              value={formData.employeeId}
              onChange={handleInputChange}
              disabled={!!initialEmployeeId || !!salary}
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

          <div>
            <label htmlFor="grossSalary" className="label">
              Gross Salary
            </label>
            <div className="relative">
              <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                <span className="text-gray-500 sm:text-sm">$</span>
              </div>
              <input
                id="grossSalary"
                name="grossSalary"
                type="number"
                min="0"
                step="0.01"
                required
                value={formData.grossSalary}
                onChange={handleInputChange}
                className="input pl-7"
              />
            </div>
          </div>
        </div>

        <div className="bg-gray-50 p-4 rounded-lg border border-gray-200">
          <h3 className="text-lg font-medium mb-4">Deductions</h3>
          
          <div className="space-y-2 mb-4">
            {deductions.length === 0 ? (
              <div className="text-gray-500 italic">No deductions added</div>
            ) : (
              <div className="space-y-2">
                {deductions.map((deduction, index) => (
                  <div key={index} className="flex items-center justify-between bg-white p-3 rounded border">
                    <div>
                      <span className="font-medium">{deduction.name}</span>
                      <span className="ml-2 text-sm text-gray-500">({deduction.type})</span>
                      <div className="text-sm">
                        {deduction.value}
                        {deduction.isPercentage ? '%' : ' $'} 
                        {deduction.isPercentage && 
                          <span className="text-gray-500">
                            (${((formData.grossSalary * deduction.value) / 100).toFixed(2)})
                          </span>
                        }
                      </div>
                    </div>
                    <button
                      type="button"
                      onClick={() => handleRemoveDeduction(index)}
                      className="text-red-600 hover:text-red-800"
                    >
                      Remove
                    </button>
                  </div>
                ))}
              </div>
            )}
          </div>

          <div className="bg-white p-4 rounded border">
            <h4 className="font-medium mb-3">Add New Deduction</h4>
            <div className="grid grid-cols-1 md:grid-cols-12 gap-3">
              <div className="md:col-span-3">
                <label htmlFor="type" className="label">Type</label>
                <select
                  id="type"
                  name="type"
                  value={newDeduction.type}
                  onChange={handleNewDeductionChange}
                  className="input"
                >
                  <option value="TAX">Tax</option>
                  <option value="INSURANCE">Insurance</option>
                  <option value="CUSTOM">Custom</option>
                </select>
              </div>
              
              <div className="md:col-span-4">
                <label htmlFor="name" className="label">Name</label>
                <input
                  id="name"
                  name="name"
                  type="text"
                  value={newDeduction.name}
                  onChange={handleNewDeductionChange}
                  className="input"
                  placeholder="e.g. Income Tax"
                />
              </div>

              <div className="md:col-span-3">
                <label htmlFor="value" className="label">Value</label>
                <input
                  id="value"
                  name="value"
                  type="number"
                  min="0"
                  step="0.01"
                  value={newDeduction.value}
                  onChange={handleNewDeductionChange}
                  className="input"
                />
              </div>

              <div className="md:col-span-2 flex items-end">
                <div className="flex items-center h-10">
                  <input
                    id="isPercentage"
                    name="isPercentage"
                    type="checkbox"
                    checked={newDeduction.isPercentage}
                    onChange={handleNewDeductionChange}
                    className="h-4 w-4 text-primary focus:ring-primary border-gray-300 rounded"
                  />
                  <label htmlFor="isPercentage" className="ml-2 block text-sm text-gray-700">
                    Percentage
                  </label>
                </div>
              </div>
            </div>
            <div className="mt-3 flex justify-end">
              <button
                type="button"
                onClick={handleAddDeduction}
                className="btn btn-secondary"
              >
                Add Deduction
              </button>
            </div>
          </div>
        </div>

        <div className="bg-blue-50 p-4 rounded-lg border border-blue-100">
          <div className="flex justify-between items-center">
            <span className="text-lg font-medium">Net Salary:</span>
            <span className="text-xl font-bold text-blue-800">
              ${netSalary.toFixed(2)}
            </span>
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
          {loading ? 'Saving...' : 'Save Salary'}
        </button>
      </div>
    </form>
  );
};

export default SalaryForm;