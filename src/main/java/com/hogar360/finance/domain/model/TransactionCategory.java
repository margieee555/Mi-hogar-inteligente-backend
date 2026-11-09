package com.hogar360.finance.domain.model;

/** Cada categoría pertenece a un solo tipo: una categoría de gasto no puede usarse en un ingreso. */
public enum TransactionCategory {

    // Gastos
    FOOD("Alimentación y mercado", TransactionType.EXPENSE),
    UTILITIES("Servicios públicos", TransactionType.EXPENSE),
    RENT("Arriendo o hipoteca", TransactionType.EXPENSE),
    TRANSPORT("Transporte", TransactionType.EXPENSE),
    HEALTH("Salud", TransactionType.EXPENSE),
    EDUCATION("Educación", TransactionType.EXPENSE),
    ENTERTAINMENT("Entretenimiento", TransactionType.EXPENSE),
    HOME_MAINTENANCE("Mantenimiento del hogar", TransactionType.EXPENSE),
    OTHER_EXPENSE("Otros gastos", TransactionType.EXPENSE),

    // Ingresos
    SALARY("Salario", TransactionType.INCOME),
    EXTRA_INCOME("Ingresos extra", TransactionType.INCOME),
    OTHER_INCOME("Otros ingresos", TransactionType.INCOME);

    private final String label;
    private final TransactionType type;

    TransactionCategory(String label, TransactionType type) {
        this.label = label;
        this.type = type;
    }

    public String getLabel() { return label; }
    public TransactionType getType() { return type; }
}