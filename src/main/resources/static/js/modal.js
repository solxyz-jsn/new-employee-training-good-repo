'use strict';

document.addEventListener('DOMContentLoaded', () => {
    const modalOverlay = document.getElementById('modal-overlay');
    let modalTrigger = null;

    function focusFirstDialogElement(modal) {
        const focusTarget = modal.querySelector('button, [href], input, select, textarea, [tabindex]:not([tabindex="-1"])') || modal;
        focusTarget.focus({preventScroll: true});
    }

    function modalOpen(modalId, trigger) {
        const modal = document.getElementById(modalId);
        if (!modal) {
            return;
        }
        modalTrigger = trigger;
        modal.classList.add('is-open');
        modal.classList.remove('hidden');
        modal.setAttribute('aria-hidden', 'false');
        modalOverlay?.classList.add('is-open');
        focusFirstDialogElement(modal);
    }

    function modalClose(modal) {
        modal.classList.remove('is-open');
        modal.classList.add('hidden');
        modal.setAttribute('aria-hidden', 'true');
        modalOverlay?.classList.remove('is-open');
        if (modalTrigger && document.contains(modalTrigger)) {
            modalTrigger.focus({preventScroll: true});
        }
        modalTrigger = null;
    }

    const modalTriggers = document.querySelectorAll('[data-modal-target]');
    const modalClosers = document.querySelectorAll('[data-modal-hide]');

    modalTriggers.forEach(trigger => {
        trigger.addEventListener('click', () => {
            const modalId = trigger.getAttribute('data-modal-target');
            modalOpen(modalId, trigger);
        });
    });

    modalClosers.forEach(closer => {
        closer.addEventListener('click', (event) => {
            event.stopPropagation();
            if (closer.classList.contains('modal-dialog') && event.target !== closer) {
                return;
            }

            const modalId = closer.getAttribute('data-modal-hide');
            const modal = modalId ? document.getElementById(modalId) : closer.closest('.modal');
            if (modal) {
                modalClose(modal);
            }
        });
    });

    const modalAria = document.getElementById('default-modal');

    if (modalAria) {
        modalAria.addEventListener('click', (event) => {
            const openedModal = document.querySelector('.modal.is-open');
            const modal = document.querySelector('[data-modal-hide=default-modal]');
            if (openedModal && modal && !modal.contains(event.target)) {
                modalClose(openedModal);
            }
        });
    }
});
