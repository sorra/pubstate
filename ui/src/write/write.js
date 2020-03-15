import isEqual from 'lodash/isEqual'
import Common from '../common.js'

class Write {

  constructor(element, targetId, draftId) {
    this.$root = $(element)
    this.targetId = targetId
    this.draftId = draftId

    this.$root.find('.btn-submit').tooltip({
      placement: 'top',
      trigger: 'manual'
    })

    this.$root.submit(() => {
      return this.onSubmit()
    })

    this.contentEditor = this.createEditor()

    this.setupEditorSwitch()
    this.setupDraftAutosave()
  }

  onSubmit() {
    try {
      let $form = this.$root
      let selectedTagIds = []
      $form.find('.tag-sel.btn-success').each(function (idx) {
        let tagId = parseInt($(this).attr('tag-id'))
        selectedTagIds.push(tagId)
      })

      let $submit = $form.find('.btn-submit')
      let $titleEdit = $('#title-edit')
      if ($titleEdit.val().length == 0) {
        $titleEdit.fadeOut().fadeIn()
        Common.popAlert('Title is empty, please input.')
        return false
      }

      let contentValue = this.contentEditor.getContent()
      $('#content-edit').val(contentValue)
      if (contentValue.length == 0) {
        $('#editor-wrapper').fadeOut().fadeIn()
        Common.popAlert('Content is empty, please input.')
        return false
      }

      $submit.prop('disabled', true)
      $form.ajaxSubmit({
        data: {
          tagIds: selectedTagIds,
          draftId: this.draftId
        },
        success: function (url) {
          console.info(url)
          // window.location = url
        },
        error: function (msg) {
          let $submit = $form.find('.btn-submit')
          Common.popAlert('Publish failed: ' + msg)
          $submit.prop('disabled', false)
        }
      })
    } catch (ex) {
      console.error(ex)
    } finally {
      return false
    }
  }

  createEditor() {
    if ($('#format').val() == 'MARKDOWN') {
      return this.createMarkdownEditor()
    } else { // HTML
      return this.createRichtextEditor()
    }
  }

  createMarkdownEditor() {
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
  
  createRichtextEditor() {
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

  setupEditorSwitch() {
    let $switchEditorDialog = $('#switch-editor-dialog')

    $switchEditorDialog.on('show.bs.modal', (event) => {
      let $link = $(event.relatedTarget)
      if (this.contentEditor.getContent().length == 0) {
        window.location = $link.data('url')
        event.preventDefault()
      }

      $switchEditorDialog.find('.sure-btn').data('url', $link.data('url'))
    })

    $switchEditorDialog.find('.sure-btn').click((event) => {
      window.location = $(event.target).data('url')
    })
  }

  getData(isFirstTime) {
    return {
      format: $('#format').val(),
      title: $('#title-edit').val(),
      content: isFirstTime ? $('#content-edit').val() : this.contentEditor.getContent()
    }
  }

  setupDraftAutosave() {
    // treat unchanged data as initial draft
    let savedDraft = this.getData(true)
  
    let saveDraft = () => {
      let liveData = this.getData()
      if (isEqual(liveData, savedDraft)) {
        setTimeout(saveDraft, 3000)
        return
      }
  
      let params = {
        draftId: this.draftId,
        targetId: this.targetId,
        format: liveData.format,
        title: liveData.title,
        content: liveData.content
      }
  
      //TODO limit concurrency to a single queue
      $.post("/drafts/save", params).done(function (resp) {
        let respDraftId = parseInt(resp)
        if (respDraftId) {
          draftId = respDraftId
        }
        savedDraft = liveData
  
        Common.popAlert('Draft is autosaved', 'info', 1000)
      }).fail(function (resp) {
        Common.popAlert('Draft autosave failed: ' + Common.toErrorMsg(resp))
      }).always(function () {
        setTimeout(saveDraft, 3000)
      })
    }
  
    setTimeout(saveDraft, 3000)
  }
}

window.Write = Write
