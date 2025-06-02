set terminal png size 800,600
set output 'us13_steam_performance.png'
set title 'US13 Performance - Steam Trains'
set xlabel 'Number of Vertices'
set ylabel 'Execution Time (ms)'
set grid
set key outside
plot 'us13_steam_data.txt' using 1:3 with linespoints title 'Time vs Vertices', \
     'us13_steam_data.txt' using 2:3 with linespoints title 'Time vs Edges'
