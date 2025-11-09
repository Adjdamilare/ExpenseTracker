package com.dami.expensetracker.services;

import com.dami.expensetracker.models.PaymentMethod;
import com.dami.expensetracker.repositories.PaymentMethodRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PaymentMethodService {

    private final PaymentMethodRepository paymentMethodRepository;

    @Autowired
    public PaymentMethodService(PaymentMethodRepository paymentMethodRepository) {
        this.paymentMethodRepository = paymentMethodRepository;
    }

    /**
     * Finds all available payment methods.
     * @return A list of all PaymentMethod entities.
     */
    public List<PaymentMethod> findAll() {
        return paymentMethodRepository.findAll();
    }
}