from dask.distributed import Client
import time
# Inicializamos el cliente (el "Host" que controla la secuencia) [5]
from dask.distributed import LocalCluster
import random
import time

if __name__ == '__main__':
    cluster = LocalCluster()
    client = cluster.get_client()
    # Función con tiempo de ejecución variable para simular desbalance
    def tarea_desbalanceada(x):
        # Simulamos una carga de trabajo desigual
        espera = random.uniform(0.5, 3.0)
        time.sleep(espera)
        return f"Tarea {x} completada en {espera:.2f}s"

    # Mapeamos 20 tareas a los trabajadores disponibles
    # En el Dask Dashboard podrás ver cómo se distribuyen (Mapping) [8]
    tareas = range(20)
    futuros_balanceo = client.map(tarea_desbalanceada, tareas)

    # Nota: Mientras esto corre, abre el link del "Dask Dashboard"
    # que aparece al crear el Client() para ver el gráfico de barras.
    print("Procesando tareas... observa el balanceo de carga en el Dashboard.")
    resultados_balanceo = client.gather(futuros_balanceo)