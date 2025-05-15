create schema if not exists product_management;

create table product_management.t_products /* Таблица товаров */
(
    id          uuid primary key, /* Идентификатор */
    name        varchar(128)   not null, /* Название */
    description text, /* Описание */
    price       numeric(10, 2) not null check (price >= 0), /* Цена */
    status      varchar(32)    not null, /* Статус товара */
    created_at  timestamptz    not null, /* Дата создания */
    updated_at  timestamptz    not null, /* Дата изменения */
    version     integer        not null /* Версия */
);

create index idx_products_name on product_management.t_products (name);
create index idx_products_status on product_management.t_products (status);

create table product_management.t_inventory /* Таблица инвентаря */
(
    id                uuid primary key, /* Идентификатор инвентаря */
    product_id        uuid    not null references product_management.t_products (id), /* Идентификатор продукта */
    quantity          integer not null check ( quantity >= 0 ), /* Количество продукта */
    reserved_quantity integer not null check ( reserved_quantity >= 0 ) /* Зарезервировано продукта */
);

create index idx_inventory_product_id on product_management.t_inventory (product_id);