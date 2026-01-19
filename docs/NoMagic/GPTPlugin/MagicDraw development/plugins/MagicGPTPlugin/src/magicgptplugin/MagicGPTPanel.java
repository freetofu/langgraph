package magicgptplugin;

import com.nomagic.magicdraw.ui.WindowComponentInfo;
import com.nomagic.magicdraw.ui.browser.WindowComponent;
import com.nomagic.magicdraw.ui.browser.WindowComponentContent;
import com.nomagic.ui.ExtendedPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MagicGPTPanel extends ExtendedPanel implements WindowComponent
{
	private final WindowComponentInfo info;
	private final JTextArea chatArea;
	private final JTextArea inputArea;
	private final DefaultListModel<File> attachmentModel;
	private final JList<File> attachmentList;
	private final MagicGPTController controller;
	private final JButton sendButton;
	private final JButton attachButton;
	private final JButton removeButton;

	public MagicGPTPanel(WindowComponentInfo info)
	{
		this.info = info;
		this.controller = new MagicGPTController();
		setLayout(new BorderLayout(8, 8));

		JLabel header = new JLabel("MagicGPT Chat");
		add(header, BorderLayout.NORTH);

		chatArea = new JTextArea(12, 40);
		chatArea.setEditable(false);
		chatArea.setLineWrap(true);
		chatArea.setWrapStyleWord(true);

		JScrollPane chatScroll = new JScrollPane(chatArea);

		inputArea = new JTextArea(3, 40);
		inputArea.setLineWrap(true);
		inputArea.setWrapStyleWord(true);

		JScrollPane inputScroll = new JScrollPane(inputArea);

		attachmentModel = new DefaultListModel<>();
		attachmentList = new JList<>(attachmentModel);
		attachmentList.setVisibleRowCount(3);
		attachmentList.setCellRenderer(new DefaultListCellRenderer()
		{
			@Override
			public Component getListCellRendererComponent(JList<?> list, Object value, int index,
				boolean isSelected, boolean cellHasFocus)
			{
				Component component = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
				if (value instanceof File)
				{
					setText(((File) value).getName());
				}
				return component;
			}
		});
		JScrollPane attachmentScroll = new JScrollPane(attachmentList);

		attachButton = new JButton("Attach");
		attachButton.addActionListener(e -> onAttach());

		removeButton = new JButton("-");
		removeButton.setToolTipText("Remove selected attachments");
		removeButton.addActionListener(e -> onRemoveAttachment());

		JPanel attachmentButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 4, 0));
		attachmentButtons.add(removeButton);
		attachmentButtons.add(attachButton);

		JPanel attachmentPanel = new JPanel(new BorderLayout(8, 0));
		attachmentPanel.add(new JLabel("Attachments"), BorderLayout.WEST);
		attachmentPanel.add(attachmentScroll, BorderLayout.CENTER);
		attachmentPanel.add(attachmentButtons, BorderLayout.EAST);

		sendButton = new JButton("Send");
		sendButton.addActionListener(this::onSend);

		JButton clearButton = new JButton("Clear");
		clearButton.addActionListener(e -> clearInput());

		JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
		buttons.add(clearButton);
		buttons.add(sendButton);

		JPanel inputPanel = new JPanel(new BorderLayout(8, 8));
		inputPanel.add(inputScroll, BorderLayout.CENTER);
		inputPanel.add(attachmentPanel, BorderLayout.NORTH);
		inputPanel.add(buttons, BorderLayout.SOUTH);

		JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, chatScroll, inputPanel);
		splitPane.setResizeWeight(0.75);
		add(splitPane, BorderLayout.CENTER);
	}

	@Override
	public WindowComponentInfo getInfo()
	{
		return info;
	}

	@Override
	public WindowComponentContent getContent()
	{
		return new MagicGPTWindowComponentContent(this);
	}

	private void onSend(ActionEvent event)
	{
		String text = inputArea.getText().trim();
		if (text.isEmpty())
		{
			return;
		}

		List<File> pending = new ArrayList<>();
		for (int i = 0; i < attachmentModel.size(); i++)
		{
			pending.add(attachmentModel.get(i));
		}
		attachmentModel.clear();

		appendLine("You: " + text);
		inputArea.setText("");
		sendButton.setEnabled(false);
		SwingWorker<String, Void> worker = new SwingWorker<>()
		{
			@Override
			protected String doInBackground()
			{
				return controller.handleQuery(text, pending);
			}

			@Override
			protected void done()
			{
				try
				{
					String response = get();
					appendLine("GPT: " + response);
				}
				catch (Exception error)
				{
					appendLine("GPT: Request failed - " + error.getMessage());
				}
				finally
				{
					sendButton.setEnabled(true);
				}
			}
		};
		worker.execute();
	}

	private void onAttach()
	{
		JFileChooser chooser = new JFileChooser();
		chooser.setMultiSelectionEnabled(true);
		int result = chooser.showOpenDialog(this);
		if (result != JFileChooser.APPROVE_OPTION)
		{
			return;
		}

		File[] selected = chooser.getSelectedFiles();
		if (selected == null || selected.length == 0)
		{
			return;
		}
		for (File file : selected)
		{
			if (file != null && file.exists())
			{
				attachmentModel.addElement(file);
			}
		}
	}

	private void clearInput()
	{
		inputArea.setText("");
		attachmentModel.clear();
	}

	private void onRemoveAttachment()
	{
		int[] indices = attachmentList.getSelectedIndices();
		if (indices == null || indices.length == 0)
		{
			return;
		}
		for (int i = indices.length - 1; i >= 0; i--)
		{
			attachmentModel.remove(indices[i]);
		}
	}

	private void appendLine(String line)
	{
		if (chatArea.getDocument().getLength() > 0)
		{
			chatArea.append("\n\n");
		}
		chatArea.append(line);
		chatArea.setCaretPosition(chatArea.getDocument().getLength());
	}
}
