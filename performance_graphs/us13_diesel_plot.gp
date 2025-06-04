set terminal png size 800,600
set output 'us13_diesel_performance.png'
set title 'US13 Performance - Diesel Trains'
set xlabel 'Number of Vertices'
set ylabel 'Execution Time (ms)'
set grid
set key outside
plot 'us13_diesel_data.txt' using 1:3 with linespoints title 'Time vs Vertices', \
     'us13_diesel_data.txt' using 2:3 with linespoints title 'Time vs Edges'
