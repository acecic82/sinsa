CREATE TABLE product (
    id bigint generated by default as identity,
    brand varchar(255),
    category varchar(255),
    price numeric(38,2),
    primary key (id)
);

create INDEX idx_brand on product(brand);
create INDEX idx_category on product(category);
create INDEX idx_price on product(price);