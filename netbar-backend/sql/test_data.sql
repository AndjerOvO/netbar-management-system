-- ============================================
-- 网咖系统测试数据
-- 密码均为 123456（BCrypt加密）
-- ============================================

-- 会员（3人：钻石/黄金/普通，余额100/5/0）
INSERT INTO netcafe_member (member_no, name, phone, password, balance, total_spent, level, status, create_time) VALUES
('M001', '张三', '13800138001', '$2a$10$m/icbryBdfmAmlj70dsHZ.8FJyPkJfYl/GawgOUJQn4zMVzsvbQe.', 100.00, 500.00, '2', '0', NOW()),
('M002', '李四', '13800138002', '$2a$10$m/icbryBdfmAmlj70dsHZ.8FJyPkJfYl/GawgOUJQn4zMVzsvbQe.', 5.00,   200.00, '1', '0', NOW()),
('M003', '王五', '13800138003', '$2a$10$m/icbryBdfmAmlj70dsHZ.8FJyPkJfYl/GawgOUJQn4zMVzsvbQe.', 0.00,   1000.00,'0', '0', NOW());

-- 机器（8台：普通区4/竞技区2/包厢2，空闲5/使用中1/故障2）
INSERT INTO netcafe_machine (machine_no, type, price_per_hour, status, create_time) VALUES
('A01', '普通区', 5.00,  '0', NOW()),
('A02', '普通区', 5.00,  '0', NOW()),
('A03', '竞技区', 10.00, '0', NOW()),
('A04', '竞技区', 10.00, '1', NOW()),
('A05', '包厢',   15.00, '0', NOW()),
('A06', '包厢',   15.00, '2', NOW()),
('B01', '普通区', 5.00,  '0', NOW()),
('B02', '普通区', 5.00,  '0', NOW());

-- 商品（6个：饮料3/零食2/外设1）
INSERT INTO netcafe_product (product_name, category, price, stock, warning_stock, status, create_time) VALUES
('可乐',       '饮料', 5.00,  100, 20, '0', NOW()),
('雪碧',       '饮料', 5.00,  80,  15, '0', NOW()),
('红牛',       '饮料', 8.00,  50,  10, '0', NOW()),
('薯片',       '零食', 6.00,  60,  12, '0', NOW()),
('泡面',       '零食', 5.00,  40,  8,  '0', NOW()),
('电竞鼠标垫', '外设', 25.00, 10,  3,  '0', NOW());
