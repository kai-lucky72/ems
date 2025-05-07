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
    if (!chartRef.current) return;

    // Destroy any existing chart
    if (chartInstance.current) {
      chartInstance.current.destroy();
    }

    // Create the chart
    const ctx = chartRef.current.getContext('2d');
    if (!ctx) return;

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
          tooltip: {
            backgroundColor: 'rgba(0, 0, 0, 0.7)',
            padding: 10,
            titleFont: {
              size: 14,
            },
            bodyFont: {
              size: 13,
            },
          },
        },
      },
    });

    // Cleanup function
    return () => {
      if (chartInstance.current) {
        chartInstance.current.destroy();
      }
    };
  }, [type, data]);

  return (
    <div className="w-full h-full min-h-[300px]">
      <canvas ref={chartRef}></canvas>
    </div>
  );
};

export default AnalyticChart;