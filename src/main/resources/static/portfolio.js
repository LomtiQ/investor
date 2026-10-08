document.addEventListener('DOMContentLoaded', () => {

    // ===== График =====
    const canvas = document.getElementById('growthChart');
    if (canvas && Array.isArray(growthData) && growthData.length > 0) {
        const labels = growthData.map(p => p.date);
        const values = growthData.map(p => p.value);

        new Chart(canvas.getContext('2d'), {
            type: 'line',
            data: {
                labels: labels,
                datasets: [{
                    label: 'Капитал, ₽',
                    data: values,
                    borderColor: '#2b8a3e',
                    backgroundColor: 'rgba(43,138,62,0.15)',
                    fill: true,
                    tension: 0.2,
                    pointRadius: 0
                }]
            },
            options: {
                responsive: true,
                plugins: {
                    legend: { display: false },
                    tooltip: {
                        callbacks: {
                            label: ctx => ctx.parsed.y.toLocaleString('ru-RU') + ' ₽'
                        }
                    }
                },
                scales: {
                    x: { ticks: { maxTicksLimit: 10 } },
                    y: {
                        ticks: {
                            callback: v => v.toLocaleString('ru-RU')
                        }
                    }
                }
            }
        });
    }

    // ===== Показ блока "новая облигация" только когда нужно =====
    const securityNameInput = document.querySelector('input[name="securityName"]');
    const securityTypeSelect = document.getElementById('securityType');
    const bondDetails = document.getElementById('bondDetails');
    const knownSecurities = Array.from(
        document.querySelectorAll('#securitiesList option')
    ).map(o => o.value.toLowerCase());

    function updateBondBlock() {
        const name = (securityNameInput.value || '').trim().toLowerCase();
        const isNew = name !== '' && !knownSecurities.includes(name);
        const isBond = securityTypeSelect.value === 'BOND';

        bondDetails.style.display = (isNew && isBond) ? 'block' : 'none';
    }

    securityNameInput.addEventListener('input', updateBondBlock);
    securityTypeSelect.addEventListener('change', updateBondBlock);
    updateBondBlock();
});