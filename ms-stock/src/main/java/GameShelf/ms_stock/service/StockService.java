package GameShelf.ms_stock.service;

import java.util.List;

import GameShelf.ms_stock.dto.StockRequestDTO;
import GameShelf.ms_stock.dto.StockResponseDTO;

public interface StockService {

    StockResponseDTO crearStock(StockRequestDTO stockRequestDTO);

    List<StockResponseDTO> listarStocks();

    StockResponseDTO buscarPorId(Long id);

    StockResponseDTO buscarPorVideojuego(Long videojuegoId);

    StockResponseDTO actualizarStock(Long id, StockRequestDTO stockRequestDTO);

    StockResponseDTO reducirStock(Long videojuegoId);

    StockResponseDTO aumentarStock(Long videojuegoId);

    List<StockResponseDTO> listarPorEstado(String estado);
}