1. run R
2. >library(bnlearn)
3. >library(rserve)
4. >Rserve(args = "--no-save")
5. click “Browse” to choose data set (data/run-1)-> “(Re)Load” to visualize and edit data -> “save” to save the modified data set. -> “Model Training and Data Processing” to build the model -> “Plot” to Visualize and edit the Bayesian network -> “save” to save the modified bayesian network (the initial values of species concentration are listed in control window. those values are also editable. we can enter different values to conduct different simulation) -> “Simulation” to visualize the concentration changes by using ODE and ODEIS models.# biosim