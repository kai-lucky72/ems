import { useState, useEffect } from 'react';
import { Deduction, Employee, Salary } from '@/types';

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
    employeeId: initialEmployeeId || (employees.length > 0 ? employees[0].id : 0),
    grossSalary: 0,
    netSalary: 0,
  });

  const [deductions, setDeductions] = useState<Deduction[]>([
    { type: 'TAX', name: 'Income Tax', value: 20, isPercentage: true },
    { type: 'INSURANCE', name: 'Health Insurance', value: 5, isPercentage: true },
  ]);

  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [newDeduction, setNewDeduction] = useState<Deduction>({
    type: 'CUSTOM',
    name: '',
    value: 0,
    isPercentage: true,
  });

  // When salary or employeeId prop changes, update the form data
  useEffect(() => {
    if (salary) {
      setFormData({
        employeeId: salary.employeeId,
        grossSalary: salary.grossSalary,
        netSalary: salary.netSalary,
      });
      setDeductions(salary.deductions);
    } else if (initialEmployeeId) {
      setFormData(prev => ({
        ...prev,
        employeeId: initialEmployeeId,
      }));
    }
  }, [salary, initialEmployeeId]);

  // Calculate net salary based on gross salary and deductions
  useEffect(() => {
    let netAmount = formData.grossSalary;
    
    deductions.forEach(deduction => {
      const deductionAmount = deduction.isPercentage
        ? (formData.grossSalary * deduction.value) / 100
        : deduction.value;
      
      netAmount -= deductionAmount;
    });
    
    setFormData(prev => ({
      ...prev,
      netSalary: netAmount > 0 ? netAmount : 0,
    }));
  }, [formData.grossSalary, deductions]);

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
    const { name, value } = e.target;
    
    if (name === 'employeeId') {
      setFormData(prev => ({ ...prev, employeeId: Number(value) }));
    } else if (name === 'grossSalary') {
      setFormData(prev => ({ ...prev, grossSalary: Number(value) }));
    }
  };

  const handleNewDeductionChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
    const { name, value, type } = e.target;
    
    if (name === 'type') {
      setNewDeduction(prev => ({ ...prev, type: value as 'TAX' | 'INSURANCE' | 'CUSTOM' }));
    } else if (name === 'name') {
      setNewDeduction(prev => ({ ...prev, name: value }));
    } else if (name === 'value') {
      setNewDeduction(prev => ({ ...prev, value: Number(value) }));
    } else if (name === 'isPercentage') {
      setNewDeduction(prev => ({ ...prev, isPercentage: type === 'checkbox' ? (e.target as HTMLInputElement).checked : prev.isPercentage }));
    }
  };

  const addDeduction = () => {
    if (!newDeduction.name.trim()) {
      setError('Deduction name is required');
      return;
    }
    
    if (newDeduction.value <= 0) {
      setError('Deduction value must be greater than 0');
      return;
    }
    
    setDeductions(prev => [...prev, { ...newDeduction }]);
    setNewDeduction({
      type: 'CUSTOM',
      name: '',
      value: 0,
      isPercentage: true,
    });
    setError('');
  };

  const removeDeduction = (index: number) => {
    setDeductions(prev => prev.filter((_, i) => i !== index));
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    
    if (formData.grossSalary <= 0) {
      setError('Gross salary must be greater than 0');
      return;
    }
    
    setLoading(true);
    setError('');

    try {
      await onSubmit({
        employeeId: formData.employeeId,
        grossSalary: formData.grossSalary,
        deductions,
      });
    } catch (err: any) {
      setError(err.message || 'Failed to save salary information. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  // Find selected employee details for display
  const selectedEmployee = employees.find(emp => emp.id === formData.employeeId);
  
  // Calculate each deduction amount for display
  const getDeductionAmount = (deduction: Deduction) => {
    return deduction.isPercentage
      ? (formData.grossSalary * deduction.value) / 100
      : deduction.value;
  };

  return (
    <form onSubmit={handleSubmit} className="space-y-6">
      {error && (
        <div className="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded">
          {error}
        </div>
      )}

      <div>
        <label htmlFor="employeeId" className="block text-sm font-medium text-gray-700">
          Employee
        </label>
        <div className="mt-1">
          <select
            id="employeeId"
            name="employeeId"
            value={formData.employeeId}
            onChange={handleChange}
            className="shadow-sm focus:ring-primary focus:border-primary block w-full sm:text-sm border-gray-300 rounded-md"
            disabled={initialEmployeeId !== null || salary !== null}
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
            {selectedEmployee.role} - {selectedEmployee.contractType.replace('_', ' ')}
          </p>
        )}
      </div>

      <div>
        <label htmlFor="grossSalary" className="block text-sm font-medium text-gray-700">
          Gross Salary
        </label>
        <div className="mt-1 relative rounded-md shadow-sm">
          <span className="absolute inset-y-0 left-0 pl-3 flex items-center text-gray-500">
            $
          </span>
          <input
            type="number"
            id="grossSalary"
            name="grossSalary"
            min="0"
            step="0.01"
            value={formData.grossSalary}
            onChange={handleChange}
            className="focus:ring-primary focus:border-primary block w-full pl-8 pr-12 sm:text-sm border-gray-300 rounded-md"
            required
          />
        </div>
      </div>

      <div className="bg-gray-50 p-4 rounded-md">
        <h3 className="text-md font-medium text-gray-800 mb-3">Deductions</h3>
        
        <div className="space-y-4">
          {deductions.map((deduction, index) => (
            <div key={index} className="flex items-center justify-between bg-white p-3 rounded shadow-sm">
              <div>
                <span className="font-medium">{deduction.name}</span>
                <span className="text-sm text-gray-500 ml-2">
                  ({deduction.type})
                </span>
                <p className="text-sm">
                  {deduction.isPercentage ? (
                    <span>{deduction.value}% (${getDeductionAmount(deduction).toFixed(2)})</span>
                  ) : (
                    <span>${deduction.value.toFixed(2)}</span>
                  )}
                </p>
              </div>
              <button
                type="button"
                onClick={() => removeDeduction(index)}
                className="text-red-600 hover:text-red-800 focus:outline-none"
              >
                <svg className="h-5 w-5" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 20 20" fill="currentColor">
                  <path fillRule="evenodd" d="M9 2a1 1 0 00-.894.553L7.382 4H4a1 1 0 000 2v10a2 2 0 002 2h8a2 2 0 002-2V6a1 1 0 100-2h-3.382l-.724-1.447A1 1 0 0011 2H9zM7 8a1 1 0 012 0v6a1 1 0 11-2 0V8zm5-1a1 1 0 00-1 1v6a1 1 0 102 0V8a1 1 0 00-1-1z" clipRule="evenodd" />
                </svg>
              </button>
            </div>
          ))}
        </div>

        {/* Add New Deduction */}
        <div className="mt-4 space-y-3">
          <h4 className="text-sm font-medium text-gray-700">Add New Deduction</h4>
          
          <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
            <div>
              <label htmlFor="deduction-type" className="block text-xs font-medium text-gray-500">
                Type
              </label>
              <select
                id="deduction-type"
                name="type"
                value={newDeduction.type}
                onChange={handleNewDeductionChange}
                className="mt-1 block w-full text-sm border-gray-300 rounded-md"
              >
                <option value="TAX">Tax</option>
                <option value="INSURANCE">Insurance</option>
                <option value="CUSTOM">Custom</option>
              </select>
            </div>
            
            <div>
              <label htmlFor="deduction-name" className="block text-xs font-medium text-gray-500">
                Name
              </label>
              <input
                type="text"
                id="deduction-name"
                name="name"
                value={newDeduction.name}
                onChange={handleNewDeductionChange}
                placeholder="e.g., Retirement Fund"
                className="mt-1 block w-full text-sm border-gray-300 rounded-md"
              />
            </div>
          </div>
          
          <div className="grid grid-cols-2 gap-3">
            <div>
              <label htmlFor="deduction-value" className="block text-xs font-medium text-gray-500">
                Value
              </label>
              <input
                type="number"
                id="deduction-value"
                name="value"
                min="0"
                step="0.01"
                value={newDeduction.value}
                onChange={handleNewDeductionChange}
                className="mt-1 block w-full text-sm border-gray-300 rounded-md"
              />
            </div>
            
            <div className="flex items-end">
              <div className="flex items-center h-10">
                <input
                  id="is-percentage"
                  name="isPercentage"
                  type="checkbox"
                  checked={newDeduction.isPercentage}
                  onChange={handleNewDeductionChange}
                  className="h-4 w-4 text-primary focus:ring-primary border-gray-300 rounded"
                />
                <label htmlFor="is-percentage" className="ml-2 block text-sm text-gray-700">
                  Is Percentage
                </label>
              </div>
            </div>
          </div>
          
          <button
            type="button"
            onClick={addDeduction}
            className="w-full flex justify-center py-2 px-4 border border-transparent text-sm font-medium rounded-md text-white bg-primary hover:bg-primary-dark focus:outline-none"
          >
            Add Deduction
          </button>
        </div>
      </div>

      <div className="bg-blue-50 p-4 rounded-md">
        <div className="flex justify-between items-center">
          <h3 className="text-md font-medium text-gray-800">Net Salary</h3>
          <span className="text-xl font-bold">${formData.netSalary.toFixed(2)}</span>
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
          {loading ? 'Saving...' : (salary ? 'Update Salary' : 'Create Salary')}
        </button>
      </div>
    </form>
  );
};

export default SalaryForm;