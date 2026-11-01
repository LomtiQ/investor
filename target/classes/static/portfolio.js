async function loadBonds() {
    const tbody = document.getElementById('bondsBody');
    if (!tbody) return;
    try {
        const res = await fetch('/api/v1/bond/get_all');
        if (!res.ok) throw new Error('HTTP ' + res.status);
        const bonds = await res.json();
        tbody.innerHTML = bonds.map(b => `
            <tr>
                <td>${escapeHtml(b.name)}</td>
                <td>${b.count ?? ''}</td>
                <td>${formatMoney(b.cost)}</td>
            </tr>
        `).join('');
    } catch (e) {
        console.error('Ошибка загрузки бумаг:', e);
    }
}

async function loadCapital() {
    const el = document.getElementById('capitalValue');
    if (!el) return;
    try {
        const res = await fetch('/api/v1/capital/get_value');
        if (!res.ok) throw new Error('HTTP ' + res.status);
        const data = await res.json();
        el.textContent = formatMoney(data.totalCapital);
    } catch (e) {
        console.error('Ошибка загрузки капитала:', e);
        el.textContent = '—';
    }
}

function initPortfolioWidget() {
    // Элементы модалки
    const modal = document.getElementById('paydModal');
    const modalTitle = document.getElementById('paydModalTitle');
    const form = document.getElementById('operationForm');
    const operationTypeInput = document.getElementById('operationType');
    const bondDetails = document.getElementById('bondDetails');

    // Кнопки
    const btnBuy = document.getElementById('btnBuy');
    const btnSell = document.getElementById('btnSell');
    const btnCoupon = document.getElementById('btnCoupon');
    const btnRedeem = document.getElementById('btnRedeem');

    if (!modal) return;

    // Функция открытия модалки
    function openOperationModal(type, title) {
        modalTitle.textContent = title;
        operationTypeInput.value = type;

        // Настраиваем видимость полей
        if (type === 'BUY') {
            // При покупке показываем все поля
            bondDetails.style.display = 'block';
            form.querySelectorAll('input, select').forEach(el => el.required = true);
        } else if (type === 'SELL') {
            // При продаже нужен только название и количество
            bondDetails.style.display = 'none';
            form.querySelector('[name="cost"]').required = false;
            form.querySelector('[name="securityType"]').required = false;
        } else {
            // Для купона и погашения скрываем лишнее
            bondDetails.style.display = 'none';
            form.querySelector('[name="cost"]').required = false;
            form.querySelector('[name="count"]').required = false;
        }

        form.reset();
        operationTypeInput.value = type; // form.reset() сбрасывает hidden поля
        document.getElementById('operationMessage').hidden = true;

        modal.hidden = false;
    }

    // Вешаем обработчики на кнопки
    if (btnBuy) btnBuy.addEventListener('click', () => openOperationModal('BUY', 'Покупка бумаги'));
    if (btnSell) btnSell.addEventListener('click', () => openOperationModal('SELL', 'Продажа бумаги'));
    if (btnCoupon) btnCoupon.addEventListener('click', () => openOperationModal('COUPON', 'Получение купона'));
    if (btnRedeem) btnRedeem.addEventListener('click', () => openOperationModal('REDEMPTION', 'Погашение бумаги'));

    // Закрытие по клику вне модалки и по Escape
    modal.addEventListener('click', (e) => {
        if (e.target === modal) modal.hidden = true;
    });
    document.addEventListener('keydown', (e) => {
        if (e.key === 'Escape' && !modal.hidden) modal.hidden = true;
    });
}

// Глобальная функция для крестика (onclick в HTML)
window.closeOperationModal = function() {
    const modal = document.getElementById('paydModal');
    if (modal) modal.hidden = true;
};

// --- ОБРАБОТКА МОДАЛЬНОГО ОКНА ОПЕРАЦИЙ ---

// Функция открытия модалки (вызывайте её при клике на "Купить", "Купон" и т.д.)
function openOperationModal(type) {
    document.getElementById('paydModal').hidden = false;
    document.getElementById('operationType').value = type;

    const title = document.getElementById('paydModalTitle');
    const existingSelectBlock = document.getElementById('existingBondSelectBlock');
    const newInputBlock = document.getElementById('newBondInputBlock');
    const bondDetails = document.getElementById('bondDetails');
    const couponDetails = document.getElementById('couponDetails');
    const securityTypeBlock = document.getElementById('securityTypeBlock');
    const countInput = document.getElementById('countInput');
    const costInput = document.getElementById('costInput');

    // Сброс полей
    document.getElementById('operationForm').reset();
    couponDetails.style.display = 'none';
    existingSelectBlock.style.display = 'none';
    newInputBlock.style.display = 'none';
    bondDetails.style.display = 'none';
    securityTypeBlock.style.display = 'block';
    countInput.readOnly = false;
    costInput.readOnly = false;

    if (type === 'BUY') {
        title.textContent = 'Покупка бумаги';
        newInputBlock.style.display = 'block';
        bondDetails.style.display = 'block';
    }
    else if (type === 'COUPON') {
        title.textContent = 'Получение купона';
        existingSelectBlock.style.display = 'block';
        securityTypeBlock.style.display = 'none'; // Скрываем тип, т.к. купон только для облигаций
        bondDetails.style.display = 'none';

        // Загружаем список облигаций для выбора
        loadBondNamesForSelect();

        // Поля количества и суммы только для чтения (рассчитываются автоматически)
        countInput.readOnly = true;
        costInput.readOnly = true;
    }
    else if (type === 'SELL') {
        title.textContent = 'Продажа бумаги';
        existingSelectBlock.style.display = 'block';
        // При продаже можно ввести количество и сумму вручную, поэтому поля активны
    }
    else if (type === 'REDEEM') {
        title.textContent = 'Погашение облигации';
        existingSelectBlock.style.display = 'block';
        securityTypeBlock.style.display = 'none';
        // При погашении количество обычно равно всему портфелю, сумма = номинал * кол-во
    }
}

function closeOperationModal() {
    document.getElementById('paydModal').hidden = true;
}

// Загрузка списка названий бумаг в select
async function loadBondNamesForSelect() {
    const select = document.getElementById('existingBondSelect');
    select.innerHTML = '<option value="">-- Выберите бумагу --</option>';

    try {
        const response = await fetch('/api/v1/bond/names');
        const names = await response.json();

        names.forEach(name => {
            const option = document.createElement('option');
            option.value = name;
            option.textContent = name;
            select.appendChild(option);
        });
    } catch (error) {
        console.error("Ошибка при загрузке списка облигаций:", error);
    }
}

// Слушатель изменения выбора в выпадающем списке
document.getElementById('existingBondSelect')?.addEventListener('change', async (e) => {
    const selectedName = e.target.value;
    const couponDetails = document.getElementById('couponDetails');
    const operationType = document.getElementById('operationType').value;

    if (!selectedName) {
        couponDetails.style.display = 'none';
        document.getElementById('countInput').value = '';
        document.getElementById('costInput').value = '';
        return;
    }

    // Запрашиваем данные по облигации с бэкенда
    try {
        const response = await fetch(`/api/bonds/info?name=${encodeURIComponent(selectedName)}`);
        if (!response.ok) throw new Error('Ошибка загрузки данных');

        const bond = await response.json();

        // Если операция - КУПОН
        if (operationType === 'COUPON') {
            couponDetails.style.display = 'block';

            // Предполагаем, что в БД есть поле count (количество на балансе)
            // Если у вас это рассчитывается иначе - поправьте эту логику
            const quantity = bond.count || 1;
            const couponPerUnit = bond.coupon || 0;
            const total = quantity * couponPerUnit;

            document.getElementById('couponPerUnit').textContent = couponPerUnit.toFixed(2);
            document.getElementById('couponQuantity').textContent = quantity;
            document.getElementById('couponTotal').textContent = total.toFixed(2);

            // Автоматически заполняем поля формы
            document.getElementById('countInput').value = quantity;
            document.getElementById('costInput').value = total;
        }
    } catch (error) {
        console.error("Ошибка:", error);
    }
});