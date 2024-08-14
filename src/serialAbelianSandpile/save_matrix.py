import os
import numpy as np
import csv

def create_matrix(rows, columns, number):
    """
    Create a matrix with specified rows and columns, filled with a specific integer number.

    Parameters:
    rows (int): Number of rows in the matrix.
    columns (int): Number of columns in the matrix.
    number (int): The integer number to fill the matrix with.

    Returns:
    np.ndarray: Matrix filled with the specified number.
    """
    matrix = np.full((rows, columns), number, dtype=int)
    return matrix

def save_matrix_to_csv(matrix, rows, columns, number):
    """
    Save the matrix to a CSV file in the 'src' folder within the current directory.

    Parameters:
    matrix (np.ndarray): The matrix to save.
    rows (int): Number of rows in the matrix.
    columns (int): Number of columns in the matrix.
    number (int): The integer number used to fill the matrix.
    """
    # Ensure the 'src' directory exists
    os.makedirs("src", exist_ok=True)
    
    # Define the file path within the 'src' directory
    filename = os.path.join("input", f"{rows}_by_{columns}_{number}.csv")
    
    # Save the matrix as a CSV file
    with open(filename, mode='w', newline='') as file:
        writer = csv.writer(file)
        writer.writerows(matrix)
    
    print(f"Matrix saved to {filename}")

# Get user input
rows = int(input("Enter the number of rows: "))
columns = int(input("Enter the number of columns: "))
number = int(input("Enter the integer number to fill the matrix: "))

# Create the matrix
matrix = create_matrix(rows, columns, number)

# Save the matrix to a CSV file in the 'src' folder
save_matrix_to_csv(matrix, rows, columns, number)
