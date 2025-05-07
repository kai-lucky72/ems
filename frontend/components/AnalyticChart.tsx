import { useEffect, useRef } from 'react';
import Chart from 'chart.js/auto';

interface ChartData {
  labels: string[];
  datasets: {
    label?: string;
    data: number[];
    backgroundColor?: string | string[];
    borderColor?: string | string[];
    tension?: number;
  }[];
}

interface AnalyticChartProps {
  type: 'bar' | 'line' | 'pie' | 'doughnut' | 'polarArea';
  data: ChartData;
}

const AnalyticChart: React.FC<AnalyticChartProps> = ({ type, data }) => {
  const chartRef = useRef<HTMLCanvasElement>(null);
  const chartInstance = useRef<Chart | null>(null);

  useEffect(() => {
    if (chartRef.current) {
      // Destroy previous chart if it exists
      if (chartInstance.current) {
        chartInstance.current.destroy();
      }

      // Create new chart
      const ctx = chartRef.current.getContext('2d');
      if (ctx) {
        chartInstance.current = new Chart(ctx, {
          type,
          data,
          options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
              legend: {
                position: 'top',
              },
              title: {
                display: false,
              },
            },
          },
        });
      }
    }

    // Cleanup on unmount
    return () => {
      if (chartInstance.current) {
        chartInstance.current.destroy();
      }
    };
  }, [type, data]);

  return <canvas ref={chartRef} />;
};

export default AnalyticChart;