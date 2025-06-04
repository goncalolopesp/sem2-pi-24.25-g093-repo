set terminal png size 800,600
set output 'us14_alllines_performance.png'
set title 'US14 Performance - All Lines'
set xlabel 'Number of Vertices'
set ylabel 'Execution Time (ms)'
set grid
set key outside
plot 'us14_alllines_data.txt' using 1:3 with linespoints title 'Time vs Vertices', \
     'us14_alllines_data.txt' using 2:3 with linespoints title 'Time vs Edges'
