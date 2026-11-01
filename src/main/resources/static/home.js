document.addEventListener('DOMContentLoaded', () => {

    // ---- инициализация виджетов ----
    if (typeof initWalletWidget === 'function')  initWalletWidget();
    if (typeof initPortfolioWidget === 'function') initPortfolioWidget();
    // if (typeof initForecastWidget === 'function') initForecastWidget();

    // ---- форма новой операции ----
    const operationForm    = document.getElementById('operationForm'); // см. правку HTML
    const operationMessage = document.getElementById('operationMessage');
    const operationSubmit  = document.getElementById('operationSubmit');

    if (operationForm) {
        operationForm.addEventListener('submit', async (e) => {
            e.preventDefault();

            const formData = new FormData(operationForm);
            const payload = {};

            // Собираем данные из формы
            formData.forEach((value, key) => {
                // Пропускаем пустые строки (например, скрытые поля при продаже)
                if (value === '') return;

                // Приводим числа к типу Number
                if (['count', 'cost', 'nominal_value', 'coupon', 'payout'].includes(key)) {
                    const n = Number(value);
                    if (!Number.isNaN(n)) payload[key] = n;
                } else {
                    payload[key] = value;
                }
            });

            operationSubmit.disabled = true;
            operationMessage.hidden = true;
            operationMessage.textContent = '';

            try {
                const res = await fetch('/api/v1/operation/new_operation', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify(payload)
                });

                if (!res.ok) {
                    const text = await res.text();
                    throw new Error(text || ('HTTP ' + res.status));
                }

                // --- УСПЕХ ---
                showMessage('Операция добавлена', 'success');

                // Закрываем модалку
                const modal = document.getElementById('paydModal');
                if (modal) modal.hidden = true;

                operationForm.reset();

                // 🔥 ВОТ СЮДА ДОБАВЛЯЕМ ВЫЗОВ ОБНОВЛЕНИЯ:
                if (typeof refreshAll === 'function') {
                    await refreshAll();
                } else {
                    // Если refreshAll нет, вызываем функции по отдельности:
                    await loadWallet();
                    await loadBonds();
                    await loadCapital();
                }
            } catch (err) {
                showMessage(err.message || 'Ошибка операции', 'error');
            } finally {
                operationSubmit.disabled = false;
            }
        });
    }

    function showMessage(text, type) {
        if (!operationMessage) return;
        operationMessage.textContent = text;
        operationMessage.classList.remove('success', 'error');
        operationMessage.classList.add(type);
        operationMessage.hidden = false;
    }

    // ---- первичная загрузка ----
    loadWallet();
    loadBonds();
    loadCapital();
    loadForecast();
});

// refreshAll — глобальная, чтобы вызывать из разных мест
async function refreshAll() {
    await Promise.all([
        loadWallet(),
        loadBonds(),
        loadCapital(),
        loadForecast()
    ]);

}