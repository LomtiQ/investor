// ======================================================
// ГРАФИК ПРОГНОЗА
// ======================================================

      let growthChartInstance = null;

      async function loadForecast() {
          try {
              const res = await fetch('/api/v1/capital/forecast');
              if (!res.ok) throw new Error('HTTP ' + res.status);
              const data = await res.json();
              renderGrowthChart(data);
          } catch (e) {
              console.error('Ошибка загрузки прогноза:', e);
          }
      }

      function renderGrowthChart(data) {
    const canvas = document.getElementById('growthChart');
    if (!canvas) return;

    // Формат {x, y} — так Chart.js с time scale работает быстро
    const chartData = data.points.map(p => ({
        x: p.date,
        y: p.value,
        event: p.event
    }));

    // Уничтожаем старый инстанс, чтобы не плодить графики
    if (growthChartInstance) {
        growthChartInstance.destroy();
        growthChartInstance = null;
    }

    const ctx = canvas.getContext('2d');
    const gradient = ctx.createLinearGradient(0, 0, 0, canvas.clientHeight || 320);
    gradient.addColorStop(0, 'rgba(43, 138, 62, 0.25)');
    gradient.addColorStop(1, 'rgba(43, 138, 62, 0)');

    growthChartInstance = new Chart(ctx, {
        type: 'line',
        data: {
            datasets: [{
                label: 'Капитал, ₽',
                data: chartData,
                borderColor: '#2b8a3e',
                backgroundColor: gradient,
                borderWidth: 3, // Сделал линию чуть толще для наглядности
                fill: true,
                tension: 0.1, // Легкое сглаживание
                pointRadius: 4, // Показываем точки, так как их всего 14
                pointBackgroundColor: '#2b8a3e',
                pointHoverRadius: 7
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            animation: false,
            // Указываем Chart.js, где искать данные в объекте
            parsing: {
                xAxisKey: 'x',
                yAxisKey: 'y'
            },
            interaction: {
                mode: 'index',
                intersect: false
            },
            plugins: {
                legend: { display: false },
                tooltip: {
                    callbacks: {
                        title: (ctx) => formatDateRu(ctx[0].raw.x),
                        label: (ctx) => {
                            const p = ctx.raw;
                            const base = ' ' + formatMoney(p.y);
                            return p.event ? `${base} — ${p.event}` : base;
                        }
                    }
                }
            },
            scales: {
                x: {
                    type: 'time',
                    time: {
                        unit: 'month', // Так как данные у вас помесячные, ставим месяц
                        displayFormats: {
                            month: 'LLL yyyy' // Формат: Ноябрь 2026
                        },
                        tooltipFormat: 'dd.MM.yyyy'
                    },
                    grid: { display: false }
                },
                y: {
                    beginAtZero: true,
                    ticks: {
                        callback: (val) => formatMoney(val)
                    }
                }
            }
        }
    });
}