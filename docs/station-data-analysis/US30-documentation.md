## **US30: Curve Fitting for Theoretical Complexity**

### **User Story Details**

As a Product Owner, I want to obtain the best-fitting curve for the sample dataset and visualize it graphically. This will help confirm that the procedure’s observed behavior aligns with the expected theoretical time complexity.

---

### **Theoretical Explanation**

#### Why curve fitting?

Curve fitting allows us to model the relationship between input size and execution time, helping determine the most likely **computational complexity** (e.g., linear, quadratic, cubic) based on real-world data.

#### What is Big O notation?

Big O notation describes how the time or space complexity of an algorithm grows relative to input size.

- **O(n)**: Linear time – grows proportionally with input size.
- **O(n²)**: Quadratic – typical of nested loops.
- **O(n³)**: Cubic – may arise from three nested loops or 3D processing.
- **O(log n)**: Logarithmic – usually from divide-and-conquer approaches.
- **O(nᵏ)**: Power law – a generalization where k can be any real number.

#### Why was **O(n³)** chosen?

Among all the tested models (linear, quadratic, cubic, logarithmic, power), the **cubic function** showed the best alignment with the data, as determined by the **R² score**:

$$
f(x) = ax^3 + bx^2 + cx + d
$$

This suggests that the procedure being analyzed scales cubically, likely due to **three nested loops** or complex inner processing. Other models like **O(log n)** or **O(2^n)** either underfit or overfit and didn’t match the practical behavior observed in the dataset.

---

### **Input & Output**

#### **Input**
- A file `Dataset_US30.xlsx` containing:
  - `size`: Input sizes.
  - `time`: Corresponding execution times.

#### **Output**
- A **plot** showing:
  - Red dots: original data.
  - Blue line: best-fitting curve.
- A **console summary** with:
  - The best-fitting function name.
  - The **R² score**.
  - The final fitted equation with calculated coefficients.

---

### **Interpretation of the Obtained Results**

After testing several curve types, the cubic function yielded the best fit based on the **coefficient of determination (R²)**. This confirms that the empirical performance aligns with an **O(n³)** complexity model, validating the theoretical assumptions.

Understanding this alignment allows teams to:
- Predict algorithm performance at larger scales.
- Optimize code with clearer expectations.
- Ensure the design meets scalability requirements.

---

### **Technologies Used**

- **Pandas & NumPy** – Data handling and processing.
- **SciPy** – Curve fitting utilities.
- **Matplotlib** – Visualization.
- **Scikit-learn** – R² score computation.

---

### **Final Notes**

Validating computational complexity with curve fitting bridges the gap between **theory and practice**, offering a measurable way to confirm whether an algorithm behaves as expected in real scenarios. This strengthens software design decisions and ensures system performance remains under control as data grows.
