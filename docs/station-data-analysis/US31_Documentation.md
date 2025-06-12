# US31-Documentation

## Overview of The User Story

As a Player, using the same data file as in the previous Sprint (Sprint 2), I want to perform a statistical analysis to determine which type of cargo contributes most significantly to the revenue of a given station, in a specific year, both defined by the user. The goal for the specified station/year is to identify the cargo (independent variable) that shows the highest correlation with revenue (dependent variable). Based on the selected cargo, a linear regression model should be aplied, presenting the error associated with the respective fit. Assuming the amount of cargo increased by 10% in January of the following year, estimate the expected revenue for January with a 95% confidence level.

---

## Theoretical Explanation

### What is a Linear Regression?

Linear regression is a statistical method used to model and analyze the relationship between two or more variables. Linear regression seeks to fit a straight line through a cloud of data points in a way that the overall distance between the points and the line is minimized.

### What is the Pearson Correlation?

Measures the linear association between X and Y using the following formula:

$$
r = \frac{\sum_{i=1}^n (X_i - \bar{X})\,(Y_i - \bar{Y})}
{\sqrt{\sum_{i=1}^n (X_i - \bar{X})^2}\;\sqrt{\sum_{i=1}^n (Y_i - \bar{Y})^2}}.
$$

The hypothesis test has the following format:

$$
H_0: \rho = 0
\quad\text{and}\quad
H_1: \rho \neq 0
$$

### Prediction & 95% Confidence Interval
We are able to calculate the average of the a certain cargo (x) for January of next year:

$$
\bar{X}_{\text{Jan}} = \frac{1}{m} \sum_{i:\,\text{month}_i=1} X_i
$$

### Increase of 10%

Assuming an increase of 10%, we can calculate it by using:

$$
X^* = 1.10 \;\times\; \bar{X}_{\text{Jan}}
$$



---

## Dependencies

```python
import pandas as pd
import numpy as np
from scipy import stats
import matplotlib.pyplot as plt 
```
---

## Required Libraries

- pandas: It is used to manipulate data and to work on analysis;
- numpy: It is used for numerical computations;
- scipy.stats: It is used for statistical functions (Pearson correlation, linear regression, etc.);
- matplotlib.pylot: It is used for data visualization;
---

## Input Data

### File Format
- Filename: 'Stations_Data.csv'
- Separator: Semicolon ';' 
- Type of file: Standard CSV Format

### Expected Columns

- station: Station name (string)
- year: Year of data (integer)
- month: Month of data (integer, from 1→12)
- revenues or revenue: Revenue data (numeric, comma-separated decimals supported)
- expenses: Expense data (numeric)

---
## Output Data

### Console Output
1. Available Stations: A list with all the stations on the file
2. Available Years: Years with data for the selected station
3. Best Cargo Type: Cargo with the highest correlation to the revenue
4. Prediction: The revenue prediction for next January (with a 10% cargo increase)
5. Confidence Interval: 95% confidence for the prediction

### Visualization

- A graphic showing the best-correlated cargo type and revenue;
- The regression line overlaid on data points;
---

## Interpretation of the obtained results

(Conclusion made having as a basis the output of the code for the Frankfurt and 2021 input)

In Frankfurt, during the year of 2021, coal is the cargo that remains on top. Using the Pearson correlation, we estimate a value of nearly 0.42, which indicates a moderate positive relationship between the coal throughput and the station income. By projecting a 10% increase on coal in January of 2022 (based on the January 2021 average), the revenue forecast seems to be around 605. The 95% confidence interval for this prediction is between [415,795]. Finally, by analyzing all the data we gathered until now, the results hint that the coal volume may lead to an increase of revenue to a certain extent, but the more data we have, the more accurate and additional predictions we are able to make. 
