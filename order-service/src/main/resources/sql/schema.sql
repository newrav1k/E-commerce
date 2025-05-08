create schema order_management;

create table order_management.t_orders
(
    id         uuid primary key,
    user_id    uuid        not null,
    status     varchar(20) not null,
    total      decimal(10, 2),
    created_at timestamp
);

create table order_management.t_order_items
(
    id         uuid primary key,
    order_id   uuid references order_management.t_orders (id),
    product_id uuid           not null,
    quantity   int            not null,
    price      decimal(10, 2) not null
);