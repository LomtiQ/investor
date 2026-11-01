// ====== ГЛОБАЛЬНЫЕ УТИЛИТЫ (общие для всех файлов) ======
function formatMoney(value) {
    return new Intl.NumberFormat('ru-RU', {
        minimumFractionDigits: 2,
        maximumFractionDigits: 2
    }).format(Number(value) || 0)
      .replace(/\u00A0/g, ' ')
      .replace(',', '.');
}

function escapeHtml(s) {
    return String(s)
        .replaceAll('&', '&amp;')
        .replaceAll('<', '&lt;')
        .replaceAll('>', '&gt;')
        .replaceAll('"', '&quot;')
        .replaceAll("'", '&#39;');
}

function formatDateRu(iso) {
    return new Date(iso).toLocaleDateString('ru-RU', {
        day: '2-digit', month: 'short', year: 'numeric'
    });
}

// ====== КОШЕЛЁК ======
async function loadWallet() {
    const walletValueEl = document.getElementById('walletValue');
    if (!walletValueEl) return;
    try {
        const res = await fetch('/api/v1/wallet/get_value');
        if (!res.ok) throw new Error('HTTP ' + res.status);
        const data = await res.json();
        walletValueEl.textContent = formatMoney(data.totalValue);
    } catch (e) {
        console.error('Ошибка загрузки кошелька:', e);
        walletValueEl.textContent = '—';
    }
}

// ====== ИНИЦИАЛИЗАЦИЯ КОШЕЛЬКА ======
function initWalletWidget() {
    const walletValueEl = document.getElementById('walletValue');
    const btnDeposit    = document.getElementById('btnDeposit');
    const btnWithdraw   = document.getElementById('btnWithdraw');
    const modal         = document.getElementById('walletModal');
    const modalTitle    = document.getElementById('modalTitle');
    const modalAmount   = document.getElementById('modalAmount');
    const modalError    = document.getElementById('modalError');
    const modalCancel   = document.getElementById('modalCancel');
    const modalSubmit   = document.getElementById('modalSubmit');

    if (!modal) return;

    let currentAction = null;

    function openWalletModal(action) {
        currentAction = action;
        modalTitle.textContent = action === 'deposit' ? 'Внести средства' : 'Снять средства';
        modalAmount.value = '';
        modalError.hidden = true;
        modalError.textContent = '';
        modal.hidden = false;
        modalAmount.focus();
    }

    function closeWalletModal() {
        modal.hidden = true;
        currentAction = null;
    }

    function showError(msg) {
        modalError.textContent = msg;
        modalError.hidden = false;
    }

    async function submitWalletModal() {
        const amount = parseFloat(modalAmount.value);
        if (!amount || amount <= 0) {
            showError('Введите положительную сумму');
            return;
        }

        const url = currentAction === 'deposit'
            ? '/api/v1/wallet/deposit'
            : '/api/v1/wallet/withdraw';

        try {
            const res = await fetch(url, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ amount: amount })
            });

            if (!res.ok) {
                const text = await res.text();
                throw new Error(text || ('HTTP ' + res.status));
            }

            closeWalletModal();
            await loadWallet();
            await loadCapital();   // чтобы обновить и капитал
        } catch (e) {
            showError(e.message);
        }
    }

    if (btnDeposit)  btnDeposit.addEventListener('click', () => openWalletModal('deposit'));
    if (btnWithdraw) btnWithdraw.addEventListener('click', () => openWalletModal('withdraw'));
    if (modalCancel) modalCancel.addEventListener('click', closeWalletModal);
    if (modalSubmit) modalSubmit.addEventListener('click', submitWalletModal);

    modal.addEventListener('click', (e) => {
        if (e.target === modal) closeWalletModal();
    });
    document.addEventListener('keydown', (e) => {
        if (e.key === 'Escape' && !modal.hidden) closeWalletModal();
    });
}