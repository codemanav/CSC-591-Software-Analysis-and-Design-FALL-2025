import pandas as pd
import numpy as np
import matplotlib.pyplot as plt
import seaborn as sns

# Load the dataset
file_path = "./data/100_Samples.csv"  
df_100_samples = pd.read_csv(file_path)

df_100_samples = df_100_samples.drop(columns=["Unnamed: 0"], errors='ignore')

df_100_samples = df_100_samples.apply(pd.to_numeric, errors='coerce')

### 1. Comprehensive Statistical Calculations ###
stats_combined = pd.DataFrame({
    "Mean": df_100_samples.mean(),
    "Median": df_100_samples.median(),
    "Variance": df_100_samples.var(),
    "Standard Deviation": df_100_samples.std(),
    "Range": df_100_samples.max() - df_100_samples.min(),
    "IQR": df_100_samples.quantile(0.75) - df_100_samples.quantile(0.25),
    "Min": df_100_samples.min(),
    "Max": df_100_samples.max()
})

# Display statistics
print("\nComprehensive Statistics for 100 Samples:\n", stats_combined)

### 2. Probability Mass Function (PMF) Construction ###
def compute_pmf(data, bins=50):
    hist, bin_edges = np.histogram(data, bins=np.linspace(data.min(), data.max(), bins+1), density=True)
    bin_centers = (bin_edges[:-1] + bin_edges[1:]) / 2
    return bin_centers, hist

# Plot PMFs for all selected variables
for column in df_100_samples.columns:
    if column != "torque":  # Exclude Torque Y
        bin_centers, pmf_values = compute_pmf(df_100_samples[column], bins=50)

        # Plot the corrected PMF
        plt.figure(figsize=(8, 5))
        plt.bar(bin_centers, pmf_values, width=np.diff(bin_centers).mean(), alpha=0.7, label=f"{column} PMF")
        plt.xlabel(column)
        plt.ylabel("Probability Density")
        plt.title(f" Probability Mass Function of {column} (50 bins)")
        plt.legend()
        plt.show()


### 3. Identifying Outliers using IQR ###
Q1 = df_100_samples.quantile(0.25)
Q3 = df_100_samples.quantile(0.75)
IQR = Q3 - Q1

outliers = {}
for column in df_100_samples.columns:
    lower_bound = Q1[column] - 1.5 * IQR[column]
    upper_bound = Q3[column] + 1.5 * IQR[column]
    outliers[column] = df_100_samples[(df_100_samples[column] < lower_bound) | (df_100_samples[column] > upper_bound)]

# Combine outliers into a single DataFrame
df_outliers = pd.concat(outliers.values()).drop_duplicates()

# Display detected outliers
print("\nOutliers in 100 Sample Dataset:\n", df_outliers)

### 4. Data Visualization ###
# Boxplots
for column in df_100_samples.columns:
    if column != "torque":  # Exclude Torque Y
        plt.figure(figsize=(8, 6))
        sns.boxplot(y=df_100_samples[column])
        plt.title(f"Boxplot of {column}")
        plt.ylabel(column)
        plt.show()

# Scatter Plots for Y (Torque) vs Each Variable
for column in df_100_samples.columns:
    if column != "torque":  # Avoid self-comparison
        plt.figure(figsize=(8, 5))
        sns.scatterplot(x=df_100_samples[column], y=df_100_samples["torque"])
        plt.xlabel(column)
        plt.ylabel("Torque (Nm)")
        plt.title(f"Scatter Plot of {column} vs Torque")
        plt.show()

# Histogram with KDE overlay for Torque (Y)
plt.figure(figsize=(8, 5))
sns.histplot(df_100_samples["torque"], kde=True, bins=30, alpha=0.6)
plt.xlabel("Torque (Nm)")
plt.title("Histogram and KDE of Torque (Y)")
plt.show()

# Density plot
plt.figure(figsize=(8,5))
sns.kdeplot(df_100_samples["torque"], fill=True)
plt.title("Density Plot of Y")
plt.xlabel("Y")
plt.ylabel("Density")
plt.show()

