@echo off
title NetBar

echo NetBar System Starting...

echo Step 1: Redis
start "Redis" C:\Users\17591\redis\redis-server.exe C:\Users\17591\redis\redis.windows.conf

echo Step 2: Backend
start "Backend" java -jar C:\Users\17591\Desktop\NetBarSystem\netbar-backend\netbar-admin\target\netbar-admin.jar

echo Step 3: Frontend
cd /d C:\Users\17591\Desktop\NetBarSystem
start "Frontend" cmd /c npm run dev

echo Done.
echo Admin: http://localhost:8088
echo Member: http://localhost:8088/member
pause
