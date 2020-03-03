function write_setup () {
  $('.btn-submit').tooltip({
    placement: 'top',
    trigger: 'manual'
  })
  $('#blog').submit(write_formSubmit)

  window.contentEditor = write_createEditor()

  write_setupSwitchEditor()

  write_setupSaveDraft()
}

function write_createEditor() {
  if ($('#format').val() == 'MARKDOWN') {
    return write_createMarkdownEditor()
  } else { // HTML
    return write_createRichtextEditor()
  }
}

function write_createMarkdownEditor() {
  return {
    simplemde: new SimpleMDE({
      element: $('#content-edit')[0],
      autoDownloadFontAwesome: false,
      spellChecker: false
    }),
    getContent: function () {
      return this.simplemde.value()
    }
  }
}

function write_createRichtextEditor() {
  tinymce.init({
    selector: '#content-edit',
    language: 'zh_CN'
  })
  return {
    getContent: function () {
      return tinymce.activeEditor.getContent()
    }
  }
}

function write_formSubmit() {
  try {
    var $form = $(this)
    var selectedTagIds = []
    $form.find('.tag-sel.btn-success').each(function (idx) {
      var tagId = parseInt($(this).attr('tag-id'))
      selectedTagIds.push(tagId)
    })

    var $submit = $form.find('.btn-submit')
    var $titleEdit = $('#title-edit')
    if ($titleEdit.val().length == 0) {
      $titleEdit.fadeOut().fadeIn()
      popAlert('请填写标题')
      return false
    }

    var contentValue = window.contentEditor.getContent()
    $('#content-edit').val(contentValue)
    if (contentValue.length == 0) {
      $('#editor-wrapper').fadeOut().fadeIn()
      popAlert('请填写内容')
      return false
    }

    $submit.prop('disabled', true)
    $form.ajaxSubmit({
      data: {tagIds: selectedTagIds, draftId: window.draftId},
      success: redirect,
      error: function (msg) {
        var $submit = $form.find('.btn-submit')
        popAlert('发表失败: ' + msg)
        $submit.prop('disabled', false)
      }
    })
  } catch (ex) {
    console.error(ex)
  } finally {
    return false
  }
}

function write_setupSwitchEditor() {
  var $switchEditorDialog = $('#switch-editor-dialog')
  $switchEditorDialog.on('show.bs.modal', function (event) {
    var $link = $(event.relatedTarget)
    if (window.contentEditor.getContent().length == 0) {
      redirect($link.data('url'))
      event.preventDefault()
    }
    var $sureBtn = $(this).find('.sure-btn')
    $sureBtn.data('url', $link.data('url'))
  })
  $switchEditorDialog.find('.sure-btn').click(function (event) {
    redirect($(event.target).data('url'))
  })
}

function write_getData() {
  return {
    format: $('#format').val(),
    title: $('#title-edit').val(),
    content: window.contentEditor.getContent()
  }
}

function write_setupSaveDraft() {
  // treat unchanged data as initial draft
  var savedDraft = write_getData()

  function saveDraft() {
    var liveData = write_getData()
    if (liveData == savedDraft) {
      setTimeout(saveDraft, 3000)
      return
    }

    var params = {
      draftId: draftId,
      targetId: articleId,
      format: liveData.format,
      title: liveData.title,
      content: liveData.content
    }

    //TODO limit concurrency to a single queue
    $.post("/drafts/save", params).done(function (resp) {
      var respDraftId = parseInt(resp)
      if (respDraftId) {
        draftId = respDraftId
      }
      savedDraft = liveData

      popAlert('Draft is saved', 'info', 1000)
    }).fail(function (resp) {
      popAlert('Draft can\'t be saved: ' + errorMsg(resp))
    }).always(function () {
      setTimeout(saveDraft, 3000)
    })
  }

  setTimeout(saveDraft, 3000)
}
