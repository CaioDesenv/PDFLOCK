
package com.mycompany.pdflock;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import org.apache.pdfbox.pdmodel.*;
import org.apache.pdfbox.pdmodel.encryption.*;

public class PDFLOCK extends JFrame {
    private JButton selectFilesButton;
    private JButton selectFolderButton;
    private JLabel statusLabel;
    private File destinationFolder;

    public PDFLOCK() {
        setTitle("Adicionar senha a PDF");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new FlowLayout());

        selectFilesButton = new JButton("Selecionar arquivos PDF");
        selectFolderButton = new JButton("Selecionar pasta de destino");
        statusLabel = new JLabel("");

        selectFilesButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setMultiSelectionEnabled(true);
                FileNameExtensionFilter filter = new FileNameExtensionFilter("PDF Files", "pdf");
                fileChooser.setFileFilter(filter);

                int result = fileChooser.showOpenDialog(null);
                if (result == JFileChooser.APPROVE_OPTION) {
                    File[] selectedFiles = fileChooser.getSelectedFiles();
                    try {
                        addPasswords(selectedFiles);
                    } catch (IOException ex) {
                        statusLabel.setText("Erro: " + ex.getMessage());
                    }
                }
            }
        });

        selectFolderButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser folderChooser = new JFileChooser();
                folderChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

                int result = folderChooser.showOpenDialog(null);
                if (result == JFileChooser.APPROVE_OPTION) {
                    destinationFolder = folderChooser.getSelectedFile();
                }
            }
        });

        add(selectFilesButton);
        add(selectFolderButton);
        add(statusLabel);

        setSize(400, 150);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void addPasswords(File[] files) throws IOException {
        if (destinationFolder == null) {
            statusLabel.setText("Selecione uma pasta de destino antes de adicionar senhas.");
            return;
        }

        List<File> processedFiles = new ArrayList<>();
        for (File file : files) {
            try (PDDocument document = PDDocument.load(file)) {
                if (!document.isEncrypted()) {
                    document.setAllSecurityToBeRemoved(true);
                    StandardProtectionPolicy policy = new StandardProtectionPolicy("12345", "12345", new AccessPermission());
                    policy.setEncryptionKeyLength(128);
                    document.protect(policy);

                    File outputFile = new File(destinationFolder, "encrypted_" + file.getName());
                    document.save(outputFile);
                    document.close();

                    processedFiles.add(outputFile);
                } else {
                    statusLabel.setText("O arquivo PDF " + file.getName() + " já está protegido por senha.");
                }
            }
        }

        statusLabel.setText("Senhas adicionadas com sucesso para os arquivos:");
        for (File file : processedFiles) {
            statusLabel.setText(statusLabel.getText() + "\n" + file.getName());
        }
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new PDFLOCK();
            }
        });
    }
}


/* detalhe para funcionar este codigo, foi necessario utilizar a biblioteca PDFBOX*/