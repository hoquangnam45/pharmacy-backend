-- Basic table
create table phone_number(
    id uuid not null primary key default gen_random_uuid(),
    country_code varchar(255) not null,
    phone_number varchar(255) not null,
    constraint unique_phone_number_code_phone_number unique(country_code, phone_number)
);

create table file_metadata(
    id uuid not null primary key default gen_random_uuid(),
    name varchar(255) not null,
    content_type varchar(255) not null,
    extension varchar(255) not null,
    path varchar(255) not null,
    created_at timestamp not null
);

create table upload_session(
    id uuid not null primary key default gen_random_uuid(),
    type varchar(255) not null,
    created_at timestamp not null,
    expired_at timestamp not null
);

create table tag(
    id uuid not null primary key default gen_random_uuid(),
    value varchar(255) not null unique
);

--

create table upload_session_file_metadata(
    id uuid not null primary key default gen_random_uuid(),
    file_metadata_id uuid unique,
    upload_session_id uuid not null,
    constraint fk_upload_session_file_metadata_file_metadata_id foreign key(file_metadata_id) references file_metadata(id),
    constraint fk_upload_session_file_metadata_upload_session_id foreign key(upload_session_id) references upload_session(id)
);

create table user_info(
      id uuid not null primary key default gen_random_uuid(),
      username varchar(255) not null unique,
      password varchar(255) not null,
      phone_id uuid unique,
      email varchar(255) unique,
      first_name varchar(255),
      last_name varchar(255),
      middle_name varchar(255),
      gender varchar(255),
      birth_date date,
      email_confirmed boolean not null default false,
      phone_number_confirmed boolean not null default false,
      constraint fk_user_info_phone_id foreign key(phone_id) references phone_number(id)
);

create table verification_code(
    id uuid not null primary key default gen_random_uuid(),
    verification_code varchar(255) not null,
    user_id uuid not null,
    type varchar(255) not null,
    last_sent_at timestamp not null,
    expired_at timestamp not null,
    constraint fk_otp_user_id foreign key(user_id) references user_info(id)
);

create table user_permission_x(
    id uuid not null primary key default gen_random_uuid(),
    role varchar(255) not null,
    user_id uuid not null,
    constraint fk_user_permission_x_user_id foreign key(user_id) references user_info(id),
    constraint unique_user_permission_x_user_role unique(user_id, role)
);

create table refresh_token(
    id uuid not null primary key default gen_random_uuid(),
    access_token varchar(500) not null unique,
    refresh_token varchar(255) not null unique,
    user_id uuid not null,
    expired_at timestamp not null,
    constraint fk_refresh_token_user_id foreign key(user_id) references user_info(id)
);

create table producer(
    id uuid not null primary key default gen_random_uuid(),
    name varchar(255) not null unique,
    country varchar(255) not null,
    created_at timestamp not null,
    updated_at timestamp not null,
    constraint unique_producer_name_country unique(name, country)
);

create table producer_audit(
    id uuid not null primary key default gen_random_uuid(),
    name varchar(255) not null unique,
    country varchar(255) not null,
    audit_object_id varchar(255) not null,
    created_at timestamp not null,
    active boolean not null default false,
    constraint unique_producer_audit_name_country unique(name, country)
);

create table medicine(
    id uuid not null primary key default gen_random_uuid(),
    name varchar(255) not null,
    description varchar(255) not null,
    short_description varchar(255) not null,
    basic_unit varchar(255) not null,
    side_effect varchar(255) not null,
    usage_type varchar(255) not null,
    producer_id uuid not null,
    created_at timestamp not null,
    updated_at timestamp not null,
    constraint unique_medicine_name_producer unique(name, producer_id),
    constraint fk_medicine_producer_id foreign key(producer_id) references producer(id)
);

create table medicine_audit(
    id uuid not null primary key default gen_random_uuid(),
    name varchar(255) not null,
    description varchar(255) not null,
    short_description varchar(255) not null,
    basic_unit varchar(255) not null,
    side_effect varchar(255) not null,
    usage_type varchar(255) not null,
    producer_id uuid not null,
    audit_object_id varchar(255) not null,
    created_at timestamp not null,
    active boolean not null default false,
    constraint unique_medicine_audit_name_producer unique(name, producer_id),
    constraint fk_medicine_audit_producer_id foreign key(producer_id) references producer_audit(id)
);

create table medicine_preview(
    id uuid not null primary key default gen_random_uuid(),
    medicine_id uuid not null,
    file_metadata_id uuid not null,
    main_preview boolean default false,
    constraint fk_medicine_preview_file_metadata_id foreign key(file_metadata_id) references file_metadata(id),
    constraint fk_medicine_preview_medicine_id foreign key(medicine_id) references medicine(id)
);

create table medicine_preview_audit(
    id uuid not null primary key default gen_random_uuid(),
    medicine_id uuid not null,
    file_metadata_id uuid not null,
    main_preview boolean not null,
    audit_object_id varchar(255) not null,
    created_at timestamp not null,
    active boolean not null default false,
    constraint fk_medicine_preview_file_metadata_id foreign key(file_metadata_id) references file_metadata(id),
    constraint fk_medicine_preview_medicine_id foreign key(medicine_id) references medicine_audit(id)
);

create table medicine_tag_x(
    id uuid not null primary key default gen_random_uuid(),
    medicine_id uuid not null,
    tag_id uuid not null,
    constraint unique_medicine_tag unique(medicine_id, tag_id),
    constraint fk_medicine_tag_x_tag_id foreign key(tag_id) references tag(id),
    constraint fk_medicine_tag_x_medicine_id foreign key(medicine_id) references medicine(id)
);

create table medicine_tag_audit_x(
    id uuid not null primary key default gen_random_uuid(),
    medicine_id uuid not null,
    tag_id uuid not null,
    audit_object_id varchar(255) not null,
    created_at timestamp not null,
    active boolean not null default false,
    constraint unique_medicine_tag_audit_x_medicine_tag unique(medicine_id, tag_id),
    constraint fk_medicine_tag_audit_x_tag_id foreign key(tag_id) references tag(id),
    constraint fk_medicine_tag_audit_x_medicine_id foreign key(medicine_id) references medicine_audit(id)
);

create table medicine_packaging_x(
    id uuid not null primary key default gen_random_uuid(),
    medicine_id uuid not null,
    packaging_unit varchar(255) not null,
    conversion_factor integer not null,
    conversion_factor_detail varchar(255) not null,
    created_at timestamp not null,
    updated_at timestamp not null,
    constraint unique_medicine_packaging_x_medicine_packaging unique(medicine_id, packaging_unit),
    constraint fk_medicine_packaging_x_medicine_id foreign key(medicine_id) references medicine(id)
);

create table medicine_packaging_audit_x(
    id uuid not null primary key default gen_random_uuid(),
    medicine_id uuid not null,
    packaging_unit varchar(255) not null,
    conversion_factor integer not null,
    conversion_factor_detail varchar(255) not null,
    audit_object_id varchar(255) not null,
    created_at timestamp not null,
    active boolean not null default false,
    constraint unique_medicine_packaging_audit_x_medicine_packaging unique(medicine_id, packaging_unit),
    constraint fk_medicine_packaging_audit_x_medicine_id foreign key(medicine_id) references medicine_audit(id)
);

create table listing(
    id uuid not null primary key default gen_random_uuid(),
    medicine_packaging_id uuid unique not null,
    price decimal not null,
    disable boolean not null default false,
    created_at timestamp not null,
    updated_at timestamp not null,
    constraint fk_listing_medicine_packaging_id foreign key(medicine_packaging_id) references medicine_packaging_x(id)
);

create table listing_audit(
    id uuid not null primary key default gen_random_uuid(),
    medicine_packaging_id uuid unique not null,
    price decimal not null,
    audit_object_id varchar(255) not null,
    created_at timestamp not null,
    active boolean not null default false,
    constraint fk_listing_audit_medicine_packaging_id foreign key(medicine_packaging_id) references medicine_packaging_audit_x(id)
);

create table delivery_info(
    id uuid not null primary key default gen_random_uuid(),
    country varchar(255) not null,
    province varchar(255) not null,
    district varchar(255) not null,
    sub_district varchar(255) not null,
    address varchar(255) not null,
    zip_code varchar(255) not null,
    phone_number_id uuid not null,
    recipient_name varchar(255),
    user_id uuid not null,
    created_at timestamp not null,
    updated_at timestamp not null,
    constraint fk_delivery_info_user_id foreign key(user_id) references user_info(id),
    constraint fk_delivery_info_phone_number_id foreign key(phone_number_id) references phone_number(id)
);

create table delivery_info_audit(
    id uuid not null primary key default gen_random_uuid(),
    country varchar(255) not null,
    province varchar(255) not null,
    district varchar(255) not null,
    sub_district varchar(255) not null,
    address varchar(255) not null,
    zip_code varchar(255) not null,
    phone_number_id uuid not null,
    recipient_name varchar(255),
    user_id uuid not null,
    audit_object_id varchar(255) not null,
    created_at timestamp not null,
    active boolean not null default false,
    constraint fk_delivery_info_audit_user_id foreign key(user_id) references user_info(id),
    constraint fk_delivery_info_audit_phone_number_id foreign key(phone_number_id) references phone_number(id)
);

create table shopping_cart_item(
    id uuid not null primary key default gen_random_uuid(),
    user_id uuid not null,
    listing_id uuid not null,
    quantity integer not null,
    constraint unique_shopping_cart_item_cart_listing unique(user_id, listing_id),
    constraint fk_shopping_cart_item_user_id foreign key(user_id) references user_info(id),
    constraint fk_shopping_cart_item_listing_id foreign key(listing_id) references listing(id)
);

create table order_info(
    id uuid not null primary key default gen_random_uuid(),
    listing_id uuid not null,
    quantity integer not null,
    status varchar(255) not null,
    delivery_info_id uuid not null,
    user_id uuid not null,
    created_at timestamp not null,
    updated_at timestamp,
    constraint fk_order_info_user_id foreign key(user_id) references user_info(id),
    constraint fk_order_info_listing_id foreign key(listing_id) references listing_audit(id),
    constraint fk_order_info_delivery_info_id foreign key(delivery_info_id) references delivery_info_audit(id)
);

create table order_item_info(
    id uuid not null primary key default gen_random_uuid(),
    listing_id uuid not null,
    quantity integer not null,
    total_price decimal not null,
    price decimal not null,
    order_id uuid not null,
    constraint fk_order_item_info_listing_id foreign key(listing_id) references listing_audit(id),
    constraint fk_order_item_info_order_id foreign key(order_id) references order_info(id)
);

create table payment_info(
    id uuid not null primary key default gen_random_uuid(),
    method varchar(255) not null,
    user_id uuid not null,
    constraint fk_payment_info_user_id foreign key(user_id) references user_info(id)
);

create table momo_payment_detail(
    id uuid not null primary key default gen_random_uuid(),
    payment_id uuid not null,
    momo_phone_number varchar(255) not null,
    constraint fk_momo_payment_detail_payment_id foreign key(payment_id) references payment_info(id)
);

create table transaction_info(
	id uuid not null primary key default gen_random_uuid(),
    order_id uuid unique not null,
    amount decimal not null,
    delivery_fee decimal not null,
    status varchar(255) not null,
    payment_id uuid not null,
    created_at timestamp not null,
    updated_at timestamp,
    deleted_at timestamp,
    constraint fk_transaction_info_order_id foreign key(order_id) references order_info(id),
    constraint fk_transaction_info_payment_id foreign key(payment_id) references payment_info(id)
);

create table comment (
  id uuid not null primary key default gen_random_uuid(),
  content text not null,
  super_comment_id uuid,
  medicine_id uuid not null,
  created_by uuid,
  created_at timestamp not null,
  updated_at timestamp,
  deleted_at timestamp,
  constraint fk_comment_medicine_id foreign key (medicine_id) references medicine(id),
  constraint fk_comment_created_by foreign key (created_by) references user_info(id),
  constraint fk_comment_super_comment_id foreign key (super_comment_id) references comment(id)
);

create table chat_room (
    id uuid not null primary key default gen_random_uuid(),
    user_id uuid not null,
    constraint fk_chat_room_user_id foreign key (user_id) references user_info(id)
);

create table chat_room_participant (
    id uuid not null primary key default gen_random_uuid(),
    user_id uuid not null,
    room_id uuid not null,
    constraint fk_chat_room_participant_user_id foreign key (user_id) references user_info (id),
    constraint fk_chat_room_participant_room_id foreign key (room_id) references chat_room (id)
);

create table chat_message (
    id uuid not null primary key default gen_random_uuid(),
    chat_room_id UUID NOT NULL,
    content TEXT NOT NULL,
    user_id UUID NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,
    deleted_at TIMESTAMP,
    constraint fk_message_user_id FOREIGN KEY (user_id) REFERENCES user_info (id),
    constraint fk_message_chat_room_id FOREIGN KEY (chat_room_id) REFERENCES chat_room (id)
);
--insert into user_info(username, password) values('example_user', 'example_password');
--insert into user_permission_x(user_id, role) values((select id from user_info where username='example_user'), 'ADMIN');

