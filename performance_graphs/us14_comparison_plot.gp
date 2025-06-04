set terminal png size 1000,600
set output 'us14_comparison_performance.png'
set title 'US14 Performance Comparison - Maintenance Types'
set xlabel 'Number of Vertices'
set ylabel 'Execution Time (ms)'
set grid
set key outside
plot 'us14_comparison_data.txt' using 1:2 with linespoints title 'All Lines', \
     'us14_comparison_data.txt' using 1:3 with linespoints title 'Electrified Only'
